package com.example.usermgmt.application.usecase;

import org.apache.poi.ss.usermodel.*;
import com.example.common.exception.ReportGenerationException;
import com.example.common.exception.UserNotFoundException;
import com.example.usermgmt.application.port.in.UserServicePort;
import com.example.usermgmt.domain.model.User;
import com.example.usermgmt.domain.port.TaskExternalServicePort;
import com.example.usermgmt.domain.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceUseCase implements UserServicePort {

    private final UserRepositoryPort userRepositoryPort;
    private final TaskExternalServicePort taskExternalServicePort;

    @Override
    public byte[] generateUserReport() {
        log.info("Starting improved report generation for all users and their last 25 tasks");

        List<User> allUsers = fetchAllUsers();

        log.info("Found {} users for report", allUsers.size());

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users and Tasks");

            ReportStyles styles = new ReportStyles(workbook);
            int rowIdx = 0;

            rowIdx = createReportHeader(sheet, rowIdx, allUsers.size(), styles);

            int totalTasksCounter = 0;
            for (User user : allUsers) {
                rowIdx = writeUserInfo(sheet, rowIdx, user, styles);
                totalTasksCounter += writeUserTasks(sheet, rowIdx, user, styles);
                rowIdx = sheet.getLastRowNum() + 2; // Espacio entre usuarios
            }

            // Update total tasks in summary (infoRow2 was at rowIdx 2 in original)
            Row infoRow2 = sheet.getRow(2);
            infoRow2.createCell(2).setCellValue("Total de Tareas Listadas:");
            infoRow2.createCell(3).setCellValue(totalTasksCounter);

            // Auto-size columns
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            log.info("Improved report generation completed successfully");
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error generating Excel report", e);
            throw new ReportGenerationException("Could not generate report", e);
        }
    }

    private List<User> fetchAllUsers() {
        List<User> allUsers = new ArrayList<>();
        int page = 0;
        int size = 100;
        Page<User> userPage;

        do {
            userPage = userRepositoryPort.findAll(PageRequest.of(page, size));
            allUsers.addAll(userPage.getContent());
            page++;
        } while (userPage.hasNext());
        return allUsers;
    }

    private int createReportHeader(Sheet sheet, int rowIdx, int totalUsers, ReportStyles styles) {
        // Report Header
        Row titleRow = sheet.createRow(rowIdx++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("REPORTE GENERAL DE USUARIOS Y TAREAS");
        titleCell.setCellStyle(styles.titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

        Row infoRow1 = sheet.createRow(rowIdx++);
        infoRow1.createCell(0).setCellValue("Fecha de Generación:");
        infoRow1.createCell(1).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Row infoRow2 = sheet.createRow(rowIdx++);
        infoRow2.createCell(0).setCellValue("Total de Usuarios:");
        infoRow2.createCell(1).setCellValue(totalUsers);

        return rowIdx + 1; // Blank row included
    }

    private int writeUserInfo(Sheet sheet, int rowIdx, User user, ReportStyles styles) {
        // User Info Header
        Row userHeaderRow = sheet.createRow(rowIdx++);
        Cell userHeaderCell = userHeaderRow.createCell(0);
        userHeaderCell.setCellValue("DATOS DEL USUARIO: " + user.getFirstName() + " " + user.getLastName());
        userHeaderCell.setCellStyle(styles.headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx - 1, rowIdx - 1, 0, 5));

        Row userDataRow = sheet.createRow(rowIdx++);
        userDataRow.createCell(0).setCellValue("ID:");
        userDataRow.createCell(1).setCellValue(user.getId());
        userDataRow.createCell(2).setCellValue("Email:");
        userDataRow.createCell(3).setCellValue(user.getEmail());

        return rowIdx;
    }

    private int writeUserTasks(Sheet sheet, int rowIdx, User user, ReportStyles styles) {
        // Tasks Table Header
        Row taskHeaderRow = sheet.createRow(rowIdx++);
        String[] taskColumns = {"Task ID", "Title", "Status", "Created At", "Description"};
        for (int i = 0; i < taskColumns.length; i++) {
            Cell cell = taskHeaderRow.createCell(i);
            cell.setCellValue(taskColumns[i]);
            cell.setCellStyle(styles.subHeaderStyle);
        }

        log.debug("Fetching last 25 tasks for user id: {}", user.getId());
        List<TaskExternalServicePort.TaskExternalDto> tasks = taskExternalServicePort.getTasksByUserId(user.getId(), 0, 25);

        if (tasks.isEmpty()) {
            Row row = sheet.createRow(rowIdx++);
            Cell cell = row.createCell(0);
            cell.setCellValue("No se encontraron tareas para este usuario.");
            cell.setCellStyle(styles.dataStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIdx - 1, rowIdx - 1, 0, 4));
            return 0;
        } else {
            for (TaskExternalServicePort.TaskExternalDto task : tasks) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(task.id());
                row.createCell(1).setCellValue(task.title());
                row.createCell(2).setCellValue(task.status());
                row.createCell(3).setCellValue(task.createdAt() != null ? task.createdAt().toString() : "N/A");
                row.createCell(4).setCellValue(task.description());

                for (int i = 0; i < taskColumns.length; i++) {
                    row.getCell(i).setCellStyle(styles.dataStyle);
                }
            }
            return tasks.size();
        }
    }

    @Override
    public Page<User> findAll(String query, Integer page, Integer size) {
        log.debug("Starting findAll operation with query: '{}', page: {}, size: {}", query, page, size);

        var pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        log.debug("PageRequest created: page={}, size={}, sort=createdAt:DESC", page, size);

        Page<User> userPage;
        if (StringUtils.isNotBlank(query)) {
            log.info("Searching users with query: '{}'", query.trim());
            userPage = userRepositoryPort.search(query.trim(), pageRequest);
            log.info("Search completed. Found {} users out of {} total", userPage.getNumberOfElements(), userPage.getTotalElements());
        } else {
            log.info("Retrieving all users");
            userPage = userRepositoryPort.findAll(pageRequest);
            log.info("Retrieved {} users out of {} total", userPage.getNumberOfElements(), userPage.getTotalElements());
        }

        return userPage;
    }

    @Override
    public User findById(Long id) {
        log.debug("Starting findById operation for user id: {}", id);
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });
        log.info("User found with id: {} - Name: {} {}", id, user.getFirstName(), user.getLastName());
        return user;
    }

    @Override
    public User save(User user) {
        log.debug("Starting save operation for user: {} {}", user.getFirstName(), user.getLastName());
        User savedUser = userRepositoryPort.save(user);
        log.info("User saved successfully with id: {} - Name: {} {}", savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName());
        return savedUser;
    }

    @Override
    public User update(Long id, User userDetails) {
        log.debug("Starting update operation for user id: {}", id);

        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for update with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });

        log.debug("User found. Updating user data: {} -> {}", user.getEmail(), userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());

        User updatedUser = userRepositoryPort.save(user);
        log.info("User updated successfully with id: {} - New data: {} {}", id, updatedUser.getFirstName(), updatedUser.getLastName());
        return updatedUser;
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Starting delete operation for user id: {}", id);

        userRepositoryPort.findById(id)
                .ifPresentOrElse(
                        user -> {
                            userRepositoryPort.deleteById(id);
                            log.info("User deleted successfully with id: {} - Name: {} {}", id, user.getFirstName(), user.getLastName());
                        },
                        () -> log.warn("User not found for deletion with id: {}", id)
                );
    }

}
