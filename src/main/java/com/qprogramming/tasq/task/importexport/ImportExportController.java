package com.qprogramming.tasq.task.importexport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskController;
import com.qprogramming.tasq.task.TaskForm;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLogService;

public class ImportExportController {

	@Autowired
	private ProjectService projectSrv;

	@Autowired
	private TaskService taskSrv;

	@Autowired
	private WorkLogService wlSrv;

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

	@RequestMapping(value = "/task/getTemplateFile", method = RequestMethod.GET)
	public @ResponseBody
	String downloadTemplate(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		URL fileURL = getClass().getResource("/template.xls");
		File file;
		try {
			file = new File(fileURL.toURI());
			if (file != null) {
				response.setHeader("content-Disposition",
						"attachment; filename=" + file.getName());
				InputStream is = new FileInputStream(file);
				IOUtils.copyLarge(is, response.getOutputStream());
			}
		} catch (URISyntaxException e) {
			LOG.error(e.getMessage());
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/task/import", method = RequestMethod.GET)
	public String startImportTasks(Model model) {
		model.addAttribute("projects", projectSrv.findAll());
		return "/task/import";
	}

	@Transactional
	@RequestMapping(value = "/task/import", method = RequestMethod.POST)
	public String importTasks(
			@RequestParam(value = "file") MultipartFile importFile,
			@RequestParam(value = "project") String projectName,
			RedirectAttributes ra, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		if (importFile.getSize() != 0) {
			try {
				String extension = FilenameUtils.getExtension(importFile
						.getOriginalFilename());
				Project project = projectSrv.findByProjectId(projectName);
				int taskCount = project.getTasks().size();
				if (extension.equals(XLS)) {
					HSSFWorkbook workbook = new HSSFWorkbook(
							importFile.getInputStream());
					HSSFSheet sheet = workbook.getSheetAt(0);
					StringBuffer logger = new StringBuffer();
					for (Iterator<Row> rowIterator = sheet.iterator(); rowIterator
							.hasNext();) {
						Row row = rowIterator.next();
						if (row.getRowNum() == 0) {
							continue;
						}
						StringBuffer log_row = verifyRow(row);
						// If there was at least one error with row , add it to
						// logger and move to next row
						if (log_row.length() > 0) {
							logger.append(log_row);
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
						if (row.getCell(DUE_DATE_CELL) != null) {
							Date date = row.getCell(DUE_DATE_CELL)
									.getDateCellValue();
							task.setDue_date(date);
						}
						// Create ID
						taskCount++;
						String taskID = project.getProjectId() + "-"
								+ taskCount;
						task.setId(taskID);
						task.setProject(project);
						project.getTasks().add(task);
						task = taskSrv.save(task);
						projectSrv.save(project);
						wlSrv.addActivityLog(task, "", LogType.CREATE);
						String log_header = "[Row " + row.getRowNum() + "]";
						logger.append(log_header);
						logger.append("Task ");
						logger.append(task);
						logger.append(" succesfully created");
						logger.append(BR);
					}
					model.addAttribute("logger", logger.toString().trim());
				} else if (extension.equals(XLM)) {
					// TODO
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOG.error(e.getLocalizedMessage());
			}
		}
		return "/task/importResults";
	}

	@RequestMapping(value = "/task/import", method = RequestMethod.POST)
	public String exportTasks(
			@RequestParam(value = "file") MultipartFile importFile,
			@RequestParam(value = "project") String projectName,
			RedirectAttributes ra, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		//TODO
		return "/task/exportResults";
	}

	private StringBuffer verifyRow(Row row) {
		StringBuffer logger = new StringBuffer();
		String log_header = "[Row " + row.getRowNum() + "]";
		for (int i = 0; i < 7; i++) {
			Cell cell = row.getCell(i);
			if ((cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK)
					&& (i != ESTIMATE_CELL & i != SP_CELL & i != DUE_DATE_CELL)) {
				logger.append(log_header);
				logger.append("Cell ");
				logger.append(COLS.charAt(i));
				logger.append(row.getRowNum());
				logger.append(" can't be empty");
				logger.append(BR);
			}
		}
		if (!isNumericCellValid(row, SP_CELL)) {
			logger.append(log_header);
			logger.append("Story points must be blank or numeric in cell ");
			logger.append(COLS.charAt(SP_CELL));
			logger.append(row.getRowNum());
			logger.append(BR);
		}
		if (!isTaskTypeValid(row)) {
			logger.append(log_header);
			logger.append("Wrong Task Priority in cell ");
			logger.append(COLS.charAt(TYPE_CELL));
			logger.append(row.getRowNum());
			logger.append(BR);
		}
		if (!isTaskPriorityValid(row)) {
			logger.append(log_header);
			logger.append("Wrong Task Priority in cell ");
			logger.append(COLS.charAt(PRIORITY_CELL));
			logger.append(row.getRowNum());
			logger.append(BR);
		}
		if (!isDATECellValid(row, DUE_DATE_CELL)) {
			logger.append(log_header);
			logger.append("Due date must be blank or date formated in cell ");
			logger.append(COLS.charAt(DUE_DATE_CELL));
			logger.append(row.getRowNum());
			logger.append(BR);
		}
		if (logger.length() > 0) {
			logger.append(log_header);
			logger.append(ROW_SKIPPED);
		}
		return logger;
	}

	private boolean isNumericCellValid(Row row, int cell) {
		return row.getCell(cell) != null
				&& row.getCell(cell).getCellType() == Cell.CELL_TYPE_NUMERIC;
	}

	private boolean isDATECellValid(Row row, int cell) {
		try {
			if (row.getCell(cell) != null
					&& row.getCell(cell).getCellType() != Cell.CELL_TYPE_BLANK) {
				if (!HSSFDateUtil.isCellDateFormatted(row.getCell(cell))) {
					return false;
				}
			}
		} catch (java.lang.IllegalStateException e) {
			return false;
		}
		return true;
	}

	private boolean isTaskTypeValid(Row row) {
		Cell cell = row.getCell(TYPE_CELL);
		if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
			try {
				TaskType.toType(row.getCell(TYPE_CELL).getStringCellValue());
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
		return true;
	}

	private boolean isTaskPriorityValid(Row row) {
		Cell cell = row.getCell(PRIORITY_CELL);
		if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
			try {
				TaskPriority.toPriority(cell.getStringCellValue());
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
		return true;
	}

}
