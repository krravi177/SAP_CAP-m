package com.sumo_management.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnSelect;
import cds.gen.sap.capire.sumo_management.Projects;
import cds.gen.sap.capire.sumo_management.Projects_;
import cds.gen.sap.capire.sumo_management.Employees;
import cds.gen.sap.capire.sumo_management.Employees_;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@ServiceName("ReportService")
public class ReportHandler implements EventHandler {

    @Autowired
    PersistenceService db;

    // Method to generate project report based on status, managerName, startDate, and endDate
    public List<String> generateProjectReport(String status, String managerName, Instant startDate, Instant endDate) {
        if (status == null || managerName == null || startDate == null || endDate == null) {
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Status, managerName, startDate, and endDate are required.");
        }

        final CqnSelect select = Select.from(Projects_.class)
                .where(p -> p.status().eq(status)
                        .and(p.assignedToEmp().empName().eq(managerName))
                        .and(p.startDate().ge(startDate))
                        .and(p.endDate().le(endDate)));

        // Execute query and retrieve project list
        List<Projects> projectList = db.run(select).listOf(Projects.class);

        // Throw an exception if no projects are found
        if (projectList.isEmpty()) {
            throw new ServiceException(ErrorStatuses.NOT_FOUND, "No projects found for the specified criteria.");
        }

        // Get the current date and time for status calculation
        LocalDateTime currentTime = LocalDateTime.now();

        // Update project status based on start and end dates
        projectList.forEach(project -> updateProjectStatus(project, currentTime));

        // Prepare response with project details as strings
        return projectList.stream()
                .map(this::formatProjectDetails)
                .collect(Collectors.toList());
    }

    private void updateProjectStatus(Projects project, LocalDateTime currentTime) {
        Instant startInstant = project.getStartDate();
        Instant endInstant = project.getEndDate();
        LocalDateTime projectStartDate = startInstant != null ? startInstant.atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
        LocalDateTime projectEndDate = endInstant != null ? endInstant.atZone(ZoneId.systemDefault()).toLocalDateTime() : null;


        if (projectStartDate != null && currentTime.isBefore(projectStartDate)) {
            project.setStatus("Not Started");
        } else if (projectEndDate != null && currentTime.isAfter(projectEndDate)) {
            project.setStatus("Late");
        } else {
            project.setStatus("On Time");
        }
    }

    private String formatProjectDetails(Projects project) {
        return "Project ID: " + project.getProjectId() +
                ", Project Name: " + project.getProjectName() +
                ", Status: " + project.getStatus() +
                ", Start Date: " + project.getStartDate() +
                ", End Date: " + project.getEndDate();
    }

    public List<String> generateEmployeeReport(Integer employeeCode) {
        if (employeeCode == null) {
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Employee code is required.");
        }

        // Construct query to fetch employees based on filters
        final CqnSelect select = Select.from(Employees_.class)
                .where(e -> e.empCode().eq(employeeCode));

        // Execute query and retrieve employee list
        List<Employees> employeeList = db.run(select).listOf(Employees.class);

        // Throw an exception if no employees are found
        if (employeeList.isEmpty()) {
            throw new ServiceException(ErrorStatuses.NOT_FOUND, "No employees found for the specified criteria.");
        }

        // Prepare response with employee details as strings
        return employeeList.stream()
                .map(this::formatEmployeeDetails)
                .collect(Collectors.toList());
    }

    private String formatEmployeeDetails(Employees employee) {
        return "Employee Code: " + employee.getEmpCode() +
                ", Employee Name: " + employee.getEmpName() +
                ", Rating: " + employee.getRating() +
                ", Time Taken: " + employee.getTimeTaken();
    }
}
