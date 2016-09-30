package com.qprogramming.tasq.task.importexport;

import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.*;
import com.qprogramming.tasq.task.importexport.xml.ProjectXML;
import com.qprogramming.tasq.task.importexport.xml.TaskXML;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class ImportExportController {

    private static final String TEMPLATE_XLS = "template.xls";
    private static final Logger LOG = LoggerFactory.getLogger(ImportExportController.class);
    private static final String COLS = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
    private static final String ID_REGEXP_PATERN = "[a-zA-Z]{1,5}-\\d+";
    private static final int NAME_CELL = 0;
    private static final int DESCRIPTION_CELL = 1;
    private static final int TYPE_CELL = 2;
    private static final int PRIORITY_CELL = 3;
    private static final int ESTIMATE_CELL = 4;
    private static final int SP_CELL = 5;
    private static final int DUE_DATE_CELL = 6;
    private static final int PARENT_CELL = 7;
    private static final String BR = "<br>";
    private static final String ROW_SKIPPED = "Row was skipped";
    private static final String NODE_SKIPPED = "Node was skipped";
    private static final String UNDERSCORE = "_";
    private static final String XML_TYPE = "xml";
    private static final String XLS_TYPE = "xls";
    private static final String DIVIDER = "<br>---------------------------------------------------<br>";
    private static final String XLSX_TYPE = "xlsx";
    private ProjectService projectSrv;
    private TaskService taskSrv;
    private WorkLogService wlSrv;
    private MessageSource msg;

    @Autowired
    public ImportExportController(ProjectService projectSrv, TaskService taskSrv, WorkLogService wlSrv,
                                  MessageSource msg) {
        this.projectSrv = projectSrv;
        this.taskSrv = taskSrv;
        this.wlSrv = wlSrv;
                this.msg = msg;
            }

            @RequestMapping(value = "/task/getTemplateFile", method = RequestMethod.GET)
            public void downloadTemplate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
                try (FileInputStream is = getExcelTemplate()) {
                    response.setHeader("content-Disposition", "attachment; filename=" + TEMPLATE_XLS);
                    IOUtils.copyLarge(is, response.getOutputStream());
                } catch (IOException e) {
                    LOG.error("Error while trying to save file , filename '{}'", TEMPLATE_XLS, e);
                } finally {
                    response.flushBuffer();
                }
            }

            @RequestMapping(value = "/task/import", method = RequestMethod.GET)
            public String startImportTasks(Model model) {
                // check if can import/create!
                if (!Roles.isUser()) {
                    throw new TasqAuthException(msg);
                }
                model.addAttribute("projects", projectSrv.findAllByUser());
                return "/task/import";
            }

            @Transactional
            @RequestMapping(value = "/task/import", method = RequestMethod.POST)
            public String importTasks(@RequestParam(value = "file") MultipartFile importFile,
                    @RequestParam(value = "project") String projectName, Model model) throws IOException {

                if (importFile.getSize() != 0) {
                    String extension = FilenameUtils.getExtension(importFile.getOriginalFilename());
                    Project project = projectSrv.findByProjectId(projectName);
                    Long taskCount = project.getLastTaskNo();
                    StringBuilder logger = new StringBuilder();
                    if (extension.equals(XLS_TYPE) || extension.equals(XLSX_TYPE)) {
                        Workbook workbook;
                try {
                    workbook = WorkbookFactory.create(importFile.getInputStream());
                    Sheet sheet = workbook.getSheetAt(0);
                    processSheet(project, taskCount, logger, sheet);
                } catch (InvalidFormatException e) {
                    LOG.error("Failed to determine excel type");
                    logger.append(e.getMessage());
                }
                model.addAttribute("logger", logger.toString().trim());
            } else if (extension.equals(XML_TYPE)) {
                try {
                    JAXBContext jaxbcontext = JAXBContext.newInstance(ProjectXML.class);
                    Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
                    File convFile = new File(importFile.getOriginalFilename());
                    importFile.transferTo(convFile);
                    ProjectXML projectXML = (ProjectXML) unmarshaller.unmarshal(convFile);
                    // validate all tasks
                    for (TaskXML taskxml : projectXML.getTaskList()) {
                        StringBuilder logRow = verifyTaskXml(taskxml);
                        if (logRow.length() > 0) {
                            logger.append(logRow);
                            continue;
                        }
                        TaskForm taskForm = new TaskForm();
                        taskForm.setName(taskxml.getName());
                        taskForm.setDescription(taskxml.getDescription());
                        taskForm.setType(taskxml.getType());
                        taskForm.setPriority(taskxml.getPriority());
                        taskForm.setEstimate(taskxml.getEstimate());
                        Task task = taskForm.createTask();
                        task.setDue_date(taskxml.getDue_date());
                        // optional fields
                        if (taskxml.getStory_points() != null) {
                            task.setStory_points(Integer.parseInt(taskxml.getStory_points()));
                        }
                        taskCount++;
                        task = finalizeTaskCretion(task, taskCount, project);
                        String logHeader = "[Task number=" + taskxml.getNumber() + "]";
                        logger.append(logHeader);
                        logger.append("Task ");
                        logger.append(task);
                        logger.append(" succesfully created");
                        logger.append(DIVIDER);

                    }
                } catch (JAXBException e) {
                    LOG.error("JAXB excetpion while importing file '{}'", importFile.getOriginalFilename(), e);
                }
                model.addAttribute("logger", logger.toString().trim());
            }
        }
        return "/task/importResults";
    }

    private void processSheet(Project project, Long taskCount, StringBuilder logger, Sheet sheet) {
        Map<Integer, Task> createdTasks = new HashMap<>();
        for (Row row : sheet) {
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
            String logHeader = "[Row " + (row.getRowNum() + 1) + "]";
            // validation finished
            TaskForm taskForm = new TaskForm();
            taskForm.setName(row.getCell(NAME_CELL).getStringCellValue());
            taskForm.setDescription(row.getCell(DESCRIPTION_CELL).getStringCellValue());
            taskForm.setType(row.getCell(TYPE_CELL).getStringCellValue());
            taskForm.setPriority(row.getCell(PRIORITY_CELL).getStringCellValue());
            if (row.getCell(ESTIMATE_CELL) != null) {
                taskForm.setEstimate(row.getCell(ESTIMATE_CELL).getStringCellValue());
            }
            Task task = taskForm.createTask();
            // optional fields
            if (row.getCell(SP_CELL) != null) {
                task.setStory_points(((Double) row.getCell(SP_CELL).getNumericCellValue()).intValue());
            }
            if (row.getCell(DUE_DATE_CELL) != null
                    && !"".equals(row.getCell(DUE_DATE_CELL).getStringCellValue())) {
                Date date = row.getCell(DUE_DATE_CELL).getDateCellValue();
                task.setDue_date(date);
            }
            Cell parentCell = row.getCell(PARENT_CELL);
            //subtask
            if (parentCell != null) {
                String parentTaskId = null;
                Task parentTask;
                if (parentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    int parentRow = (int) parentCell.getNumericCellValue();
                    parentTask = createdTasks.get(parentRow);
                    if (parentTask != null) {
                        parentTaskId = parentTask.getId();
                    } else {
                        parentTaskId = "" + parentRow;
                    }
                } else {
                    parentTaskId = parentCell.getStringCellValue();
                }
                //get real task ( should be created by now)
                parentTask = taskSrv.findById(parentTaskId);
                if (parentTask != null) {
                    task = taskSrv.createSubTask(project, parentTask, task);
                    logger.append(logHeader);
                    logger.append("Subtask ");
                    logger.append(task);
                    logger.append(" successfully created");
                    logger.append(DIVIDER);
                } else {
                    logger.append(logHeader);
                    logger.append(String.format("Parent '%s' was not found either in excel nor in database.", parentTaskId));
                    logger.append(BR);
                    logger.append(logHeader);
                    logger.append(ROW_SKIPPED);
                    logger.append(DIVIDER);
                }
            } else {
                taskCount++;
                task = finalizeTaskCretion(task, taskCount, project);
                createdTasks.put(row.getRowNum() + 1, task);
                logger.append(logHeader);
                logger.append("Task ");
                logger.append(task);
                logger.append(" succesfully created");
                logger.append(DIVIDER);
            }
        }
    }

    private Task finalizeTaskCretion(Task task, Long taskCount, Project project) {
        String taskID = project.getProjectId() + "-" + taskCount;
        task.setId(taskID);
        task.setProject(project);
        task.setTaskOrder(taskCount);
        if (taskCount != null) {
            project.getTasks().add(task);
            project.setLastTaskNo(taskCount);
        }
        task = taskSrv.save(task);
        projectSrv.save(project);
        wlSrv.addActivityLog(task, "", LogType.CREATE);
        return task;
    }

    @RequestMapping(value = "/task/export", method = RequestMethod.GET)
    public void exportTasks(@RequestParam(value = "tasks") String[] tasks, @RequestParam(value = "type") String type,
                            HttpServletResponse response, HttpServletRequest request) throws IOException {
        // Prepare task list
        List<Task> taskList = taskSrv.finAllById(Arrays.asList(tasks));
        //we should have only one project, and exporting account should be able to view it.
        Set<Project> projects = taskList.stream().map(Task::getProject).collect(Collectors.toSet());
        Project project = projects.iterator().next();
        if (projects.size() > 1 || !projectSrv.canView(project)) {
            throw new TasqAuthException(msg, "task.export.tampering");
        }
        FileInputStream is = getExcelTemplate();
        String filename = project.getProjectId() + UNDERSCORE + Utils.convertDateToString(new Date()) + UNDERSCORE
                + "export";
        ServletOutputStream out = response.getOutputStream();
        if (type.equals(XML_TYPE)) {
            // pack into xml
            ProjectXML projectXML = new ProjectXML();
            projectXML.setName(project.getName());
            ArrayList<TaskXML> xmltasklist = new ArrayList<>();
            int count = 0;
            for (Task task : taskList) {
                TaskXML taskXML = new TaskXML(task);
                taskXML.setNumber(count);
                xmltasklist.add(taskXML);
                count++;
            }
            projectXML.setTaskList(xmltasklist);
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(ProjectXML.class);
                Marshaller marshaller = jaxbContext.createMarshaller();
                Utils.setHttpRequest(request);
                // String templateURL = Utils.getBaseURL()
                // + "/export_template.xsl";
                // marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
                // templateURL);
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                // // OutputStream output = new BufferedOutputStream(out);
                response.setContentType("application/xml");
                response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".xml");
                marshaller.marshal(projectXML, out);
            } catch (JAXBException e) {
                LOG.error("JAXB excetpion while exporting '{}'", e);
            }
        } else if (type.equals(XLS_TYPE)) {
            // pack into excel
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            HSSFSheet sheet = workbook.getSheetAt(0);
            int rowNo = 1;
            for (Task task : taskList) {
                HSSFRow row = sheet.createRow(rowNo++);
                row.createCell(NAME_CELL).setCellValue(task.getName());
                row.createCell(DESCRIPTION_CELL).setCellValue(task.getDescription());
                row.createCell(TYPE_CELL).setCellValue(((TaskType) task.getType()).getEnum());
                row.createCell(PRIORITY_CELL).setCellValue(task.getPriority().toString());
                row.createCell(ESTIMATE_CELL).setCellValue(task.getEstimate());
                row.createCell(SP_CELL).setCellValue(task.getStory_points());
                row.createCell(DUE_DATE_CELL).setCellValue(task.getDue_date());
            }
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".xls");
            workbook.write(out);
        }
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
    private FileInputStream getExcelTemplate() throws IOException {
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
        String logHeader = "[Row " + (row.getRowNum() + 1) + "]";
        for (int i = 0; i < 7; i++) {
            Cell cell = row.getCell(i);
            if ((cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK)
                    && (i != ESTIMATE_CELL & i != SP_CELL & i != DUE_DATE_CELL)) {
                logger.append(logHeader);
                logger.append("Cell ");
                logger.append(COLS.charAt(i));
                logger.append(row.getRowNum() + 1);
                logger.append(" can't be empty");
                logger.append(BR);
            }
        }
        if (!isNumericCellValid(row, SP_CELL)) {
            logger.append(logHeader);
            logger.append("Story points must be blank or numeric in cell ");
            logger.append(COLS.charAt(SP_CELL));
            logger.append(row.getRowNum() + 1);
            logger.append(BR);
        }
        if (!isTaskTypeValid(row)) {
            logger.append(logHeader);
            logger.append("Wrong Task Type in cell ");
            logger.append(COLS.charAt(TYPE_CELL));
            logger.append(row.getRowNum() + 1);
            logger.append(BR);
        }
        if (!isTaskPriorityValid(row)) {
            logger.append(logHeader);
            logger.append("Wrong Task Priority in cell ");
            logger.append(COLS.charAt(PRIORITY_CELL));
            logger.append(row.getRowNum() + 1);
            logger.append(BR);
        }
        if (!isDATECellValid(row, DUE_DATE_CELL)) {
            logger.append(logHeader);
            logger.append("Due date must be blank or date formated in cell ");
            logger.append(COLS.charAt(DUE_DATE_CELL));
            logger.append(row.getRowNum() + 1);
            logger.append(BR);
        }
        if (!isParentTaskValid(row)) {
            logger.append(logHeader);
            logger.append("Parent cell have wrong value in cell ");
            logger.append(COLS.charAt(PARENT_CELL));
            logger.append(row.getRowNum() + 1);
            logger.append(BR);
        }
        if (logger.length() > 0) {
            logger.append(logHeader);
            logger.append(ROW_SKIPPED);
            logger.append(DIVIDER);
        }
        return logger;
    }

    private StringBuilder verifyTaskXml(TaskXML task) {
        StringBuilder logger = new StringBuilder();
        String logHeader = "[Task number=" + task.getNumber() + "]";
        if (task.getName() == null) {
            logger.append(logHeader);
            logger.append("Name can't be empty");
            logger.append(BR);
        }
        if (task.getDescription() == null) {
            logger.append(logHeader);
            logger.append("Description can't be empty");
            logger.append(BR);
        }
        if (task.getDescription() == null) {
            logger.append(logHeader);
            logger.append("Description can't be empty");
            logger.append(BR);
        }
        if (task.getType() == null || !isTaskTypeValid(task.getType())) {
            logger.append(logHeader);
            logger.append("Empty or wrong task type");
            logger.append(BR);
        }
        if (task.getPriority() == null || !isTaskPriorityValid(task.getPriority())) {
            logger.append(logHeader);
            logger.append("Empty or wrong task priority");
            logger.append(BR);
        }
        if (task.getStory_points() != null && !isNumerical(task.getStory_points())) {
            logger.append(logHeader);
            logger.append("Story points must be empty or a number");
            logger.append(BR);
        }
        if (logger.length() > 0) {
            logger.append(logHeader);
            logger.append(NODE_SKIPPED);
            logger.append(DIVIDER);
        }
        return logger;
    }

    private boolean isNumerical(String storyPoints) {
        try {
            Integer.parseInt(storyPoints);
            return true;
        } catch (NumberFormatException e) {
            LOG.error(e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Check if cell in row is numeric type
     *
     * @param row
     * @param cell
     * @return
     */
    private boolean isNumericCellValid(Row row, int cell) {
        return !(row.getCell(cell) != null)
                || (row.getCell(cell) != null && row.getCell(cell).getCellType() == Cell.CELL_TYPE_NUMERIC);
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
            if (row.getCell(cell) != null && !"".equals(row.getCell(cell).getStringCellValue())
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
     * @return
     */
    private boolean isTaskTypeValid(Row row) {
        Cell cell = row.getCell(TYPE_CELL);
        return !(cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) || isTaskTypeCellValid(row);
    }

    private boolean isTaskTypeCellValid(Row row) {
        String type = row.getCell(TYPE_CELL).getStringCellValue();
        try {
            return taskTypeValid(row, type);
        } catch (IllegalArgumentException e) {
            LOG.error(e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Check if row have valid task type with parent as well
     *
     * @param row
     * @param type
     * @return
     */
    private boolean taskTypeValid(Row row, String type) {
        TaskType taskType = TaskType.toType(type);
        if (taskType.isSubtask()) {
            return !parentCellEmpty(row);
        } else {
            return parentCellEmpty(row);
        }
    }

    private boolean isTaskTypeValid(String type) {
        try {
            TaskType.toType(type);
            return true;
        } catch (IllegalArgumentException e) {
            LOG.error(e.getLocalizedMessage());
            return false;
        }
    }


    /**
     * Check if cell in row is has correct TaskPriority value
     *
     * @param row
     * @return
     */
    private boolean isTaskPriorityValid(Row row) {
        Cell cell = row.getCell(PRIORITY_CELL);
        return !(cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) || isTaskPriorityValid(cell.getStringCellValue());
    }

    /**
     * Check basic validation if parent cell has correct numerical value or ID is correct format
     *
     * @param row
     * @return
     */
    private boolean isParentTaskValid(Row row) {
        if (parentCellEmpty(row)) {
            return true;
        }
        Cell parentcell = row.getCell(PARENT_CELL);
        if (parentcell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            double parentRow = parentcell.getNumericCellValue();
            return parentRow < row.getRowNum()+1;
        } else if (parentcell.getCellType() == Cell.CELL_TYPE_STRING) {
            Pattern r = Pattern.compile(ID_REGEXP_PATERN);
            Matcher m = r.matcher(parentcell.getStringCellValue());
            return m.matches();
        }
        return false;
    }

    private boolean parentCellEmpty(Row row) {
        Cell cell = row.getCell(PARENT_CELL);
        return cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK;
    }

    private boolean isTaskPriorityValid(String priority) {
        try {
            TaskPriority.toPriority(priority);
            return true;
        } catch (IllegalArgumentException e) {
            LOG.error(e.getLocalizedMessage());
            return false;
        }
    }

}
