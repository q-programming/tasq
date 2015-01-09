package com.qprogramming.tasq.task.importexport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskForm;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@Controller
public class ImportExportController {

	private static final String TEMPLATE_XLS = "template.xls";

	@Autowired
	private ProjectService projectSrv;

	@Autowired
	private TaskService taskSrv;

	@Autowired
	private WorkLogService wlSrv;

	@Autowired
	private MessageSource msg;

	private static final Logger LOG = LoggerFactory
			.getLogger(ImportExportController.class);
	private static final String COLS = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
	private static final Object XLS = "xls";
	private static final Object XLM = "xml";
	private static final int NAME_CELL = 0;
	private static final int DESCRIPTION_CELL = 1;
	private static final int TYPE_CELL = 2;
	private static final int PRIORITY_CELL = 3;
	private static final int ESTIMATE_CELL = 4;
	private static final int SP_CELL = 5;
	private static final int DUE_DATE_CELL = 6;
	private static final String BR = "<br>";
	private static final Object ROW_SKIPPED = "Row was skipped</br>";
	private static final String UNDERSCORE = "_";

	@RequestMapping(value = "/task/getTemplateFile", method = RequestMethod.GET)
	public @ResponseBody
	String downloadTemplate(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		FileInputStream is = getExcelTemplate();
		response.setHeader("content-Disposition", "attachment; filename="
				+ TEMPLATE_XLS);
		IOUtils.copyLarge(is, response.getOutputStream());
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/task/import", method = RequestMethod.GET)
	public String startImportTasks(Model model) {
		// check if can import/create!
		if (!Roles.isReporter()) {
			throw new TasqAuthException(msg);
		}
		model.addAttribute("projects", projectSrv.findAllByUser());
		return "/task/import";
	}

	@Transactional
	@RequestMapping(value = "/task/import", method = RequestMethod.POST)
	public String importTasks(
			@RequestParam(value = "file") MultipartFile importFile,
			@RequestParam(value = "project") String projectName, Model model)
			throws IOException {

		if (importFile.getSize() != 0) {
			String extension = FilenameUtils.getExtension(importFile
					.getOriginalFilename());
			Project project = projectSrv.findByProjectId(projectName);
			int taskCount = project.getTasks().size();
			if (extension.equals(XLS)) {
				HSSFWorkbook workbook = new HSSFWorkbook(
						importFile.getInputStream());
				HSSFSheet sheet = workbook.getSheetAt(0);
				StringBuilder logger = new StringBuilder();
				for (Iterator<Row> rowIterator = sheet.iterator(); rowIterator
						.hasNext();) {
					Row row = rowIterator.next();
					if (row.getRowNum() == 0) {
						continue;
					}
					StringBuilder logRow = verifyRow(row);
					// If there was at least one error with row , add it to
					// logger and move to next row
					if (logRow.length() > 0) {
						logger.append(logRow);
						continue;
					}
					// validation finished
					TaskForm taskForm = new TaskForm();
					taskForm.setName(row.getCell(NAME_CELL)
							.getStringCellValue());
					taskForm.setDescription(row.getCell(DESCRIPTION_CELL)
							.getStringCellValue());
					taskForm.setType(row.getCell(TYPE_CELL)
							.getStringCellValue());
					taskForm.setPriority(row.getCell(PRIORITY_CELL)
							.getStringCellValue());
					if (row.getCell(ESTIMATE_CELL) != null) {
						taskForm.setEstimate(row.getCell(ESTIMATE_CELL)
								.getStringCellValue());
					}
					Task task = taskForm.createTask();
					// optional fields
					if (row.getCell(SP_CELL) != null) {
						task.setStory_points(((Double) row.getCell(SP_CELL)
								.getNumericCellValue()).intValue());
					}
					if (row.getCell(DUE_DATE_CELL) != null
							&& !"".equals(row.getCell(DUE_DATE_CELL)
									.getStringCellValue())) {
						Date date = row.getCell(DUE_DATE_CELL)
								.getDateCellValue();
						task.setDue_date(date);
					}
					// Create ID
					taskCount++;
					String taskID = project.getProjectId() + "-" + taskCount;
					task.setId(taskID);
					task.setProject(project);
					project.getTasks().add(task);
					task = taskSrv.save(task);
					projectSrv.save(project);
					wlSrv.addActivityLog(task, "", LogType.CREATE);
					String logHeader = "[Row " + row.getRowNum() + "]";
					logger.append(logHeader);
					logger.append("Task ");
					logger.append(task);
					logger.append(" succesfully created");
					logger.append(BR);
				}
				model.addAttribute("logger", logger.toString().trim());
			} else if (extension.equals(XLM)) {
				// TODO
			}
		}
		return "/task/importResults";
	}

	@RequestMapping(value = "/task/export", method = RequestMethod.POST)
	public void exportTasks(@RequestParam(value = "tasks") String[] idList,
			HttpServletResponse response) throws FileNotFoundException,
			IOException {
		// Prepare task list
		List<Task> taskList = new LinkedList<Task>();
		Project project = null;
		for (int i = 0; i < idList.length; i++) {
			Task task = taskSrv.findById(idList[i]);
			if (project == null) {
				project = task.getProject();
			}
			if (task != null) {
				taskList.add(task);
			}
		}
		FileInputStream is = getExcelTemplate();
		String filename = project.getProjectId() + UNDERSCORE
				+ Utils.convertDateToString(new Date()) + UNDERSCORE
				+ "export.xls";
		// pack into excel
		HSSFWorkbook workbook = new HSSFWorkbook(is);
		HSSFSheet sheet = workbook.getSheetAt(0);
		int rowNo = 1;
		for (Task task : taskList) {
			HSSFRow row = sheet.createRow(rowNo++);
			row.createCell(NAME_CELL).setCellValue(task.getName());
			row.createCell(DESCRIPTION_CELL)
					.setCellValue(task.getDescription());
			row.createCell(TYPE_CELL).setCellValue(
					((TaskType) task.getType()).getEnum());
			row.createCell(PRIORITY_CELL).setCellValue(
					((TaskPriority) task.getPriority()).toString());
			row.createCell(ESTIMATE_CELL).setCellValue(task.getEstimate());
			row.createCell(SP_CELL).setCellValue(task.getStory_points());
			row.createCell(DUE_DATE_CELL).setCellValue(task.getDue_date());
		}
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ filename);
		ServletOutputStream out = response.getOutputStream();
		workbook.write(out);
		out.flush();
		out.close();
	}

	/**
	 * Returns excel template
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private FileInputStream getExcelTemplate() throws FileNotFoundException,
			IOException {
		URL fileURL = getClass().getResource("/" + TEMPLATE_XLS);
		File file;
		try {
			file = new File(fileURL.toURI());
			if (file != null) {
				return new FileInputStream(file);
			}
		} catch (URISyntaxException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Check if row is valid
	 * 
	 * @param row
	 * @return
	 */
	private StringBuilder verifyRow(Row row) {
		StringBuilder logger = new StringBuilder();
		String logHeader = "[Row " + row.getRowNum() + "]";
		for (int i = 0; i < 7; i++) {
			Cell cell = row.getCell(i);
			if ((cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK)
					&& (i != ESTIMATE_CELL & i != SP_CELL & i != DUE_DATE_CELL)) {
				logger.append(logHeader);
				logger.append("Cell ");
				logger.append(COLS.charAt(i));
				logger.append(row.getRowNum());
				logger.append(" can't be empty");
				logger.append(BR);
			}
		}
		if (!isNumericCellValid(row, SP_CELL)) {
			logger.append(logHeader);
			logger.append("Story points must be blank or numeric in cell ");
			logger.append(COLS.charAt(SP_CELL));
			logger.append(row.getRowNum());
			logger.append(BR);
		}
		if (!isTaskTypeValid(row)) {
			logger.append(logHeader);
			logger.append("Wrong Task Type in cell ");
			logger.append(COLS.charAt(TYPE_CELL));
			logger.append(row.getRowNum());
			logger.append(BR);
		}
		if (!isTaskPriorityValid(row)) {
			logger.append(logHeader);
			logger.append("Wrong Task Priority in cell ");
			logger.append(COLS.charAt(PRIORITY_CELL));
			logger.append(row.getRowNum());
			logger.append(BR);
		}
		if (!isDATECellValid(row, DUE_DATE_CELL)) {
			logger.append(logHeader);
			logger.append("Due date must be blank or date formated in cell ");
			logger.append(COLS.charAt(DUE_DATE_CELL));
			logger.append(row.getRowNum());
			logger.append(BR);
		}
		if (logger.length() > 0) {
			logger.append(logHeader);
			logger.append(ROW_SKIPPED);
		}
		return logger;
	}

	/**
	 * Check if cell in row is numeric type
	 * 
	 * @param row
	 * @param cell
	 * @return
	 */
	private boolean isNumericCellValid(Row row, int cell) {
		return row.getCell(cell) != null
				&& row.getCell(cell).getCellType() == Cell.CELL_TYPE_NUMERIC;
	}

	/**
	 * Check if cell in row is Date type
	 * 
	 * @param row
	 * @param cell
	 * @return
	 */
	private boolean isDATECellValid(Row row, int cell) {
		try {
			if (row.getCell(cell) != null
					&& !"".equals(row.getCell(cell).getStringCellValue())
					&& (!HSSFDateUtil.isCellDateFormatted(row.getCell(cell)))) {
				return false;
			}
		} catch (java.lang.IllegalStateException e) {
			LOG.error(e.getLocalizedMessage());
			return false;
		}
		return true;
	}

	/**
	 * Check if cell in row is has correct TaskType value
	 * 
	 * @param row
	 * @param cell
	 * @return
	 */
	private boolean isTaskTypeValid(Row row) {
		Cell cell = row.getCell(TYPE_CELL);
		if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
			try {
				TaskType.toType(row.getCell(TYPE_CELL).getStringCellValue());
				return true;
			} catch (IllegalArgumentException e) {
				LOG.error(e.getLocalizedMessage());
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if cell in row is has correct TaskPriority value
	 * 
	 * @param row
	 * @param cell
	 * @return
	 */
	private boolean isTaskPriorityValid(Row row) {
		Cell cell = row.getCell(PRIORITY_CELL);
		if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
			try {
				TaskPriority.toPriority(cell.getStringCellValue());
				return true;
			} catch (IllegalArgumentException e) {
				LOG.error(e.getLocalizedMessage());
				return false;
			}
		}
		return true;
	}

}
