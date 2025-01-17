namespace sap.capire.sumo_management;
 
entity Projects {
    key projectId    : Integer;
    projectName      : String;
    assignedToP      : Integer;    
    timeAssigned     : Integer;
    projectChanges   : Integer;
    clientName       : String;
    description      : String;
    urgency          : String;
    createdBy        : Integer;
    modifiedBy       : Integer;
    cost             : Decimal;

    // New fields added
    startDate        : DateTime;  
    endDate          : DateTime;
    status           : String;
  

    // Associations
    createdByEmp     : Association to Employees on createdByEmp.empCode = createdBy;
    modifiedByEmp    : Association to Employees on modifiedByEmp.empCode = modifiedBy;
    subProjects      : Association to many SubProjects on subProjects.projectId = projectId;
    assignedToEmp    : Association to Employees on assignedToEmp.empCode = assignedToP;  
}

entity Employees {
    key empCode      : Integer;
    empName          : String;
    projectId        : Integer;
    rating           : Integer;
    managerId        : Integer;
    timeTaken        : Integer;
    permissionToEdit : String;
    permissionToView : String;
    permissionToCreateP : String;
    permissionToCreateSP : String;
    permissionToCreateE : String;
    
 
    // Associations to Projects and SubProjects where the employee is assigned
    projects         : Association to many Projects on projects.assignedToP = empCode;  
    empManager : Association to Employees on empManager.empCode = managerId;
    subProjects      : Association to many SubProjects on subProjects.assignedToE = empCode;  
}
 
entity SubProjects {
    key moduleId     : Integer;
    projectId        : Integer;
    moduleName       : String;
    assignedToE      : Integer;   
    projectManagerId : Integer;
    timeAssigned     : Integer;
    moduleStatus     : String;
    createdBy        : Integer;
    modifiedBy       : Integer;

    // New fields added
    startDate        : DateTime;  
    endDate          : DateTime;  
    status           : String;    
    // Associations
    project          : Association to Projects on project.projectId = projectId;
    createdByEmp     : Association to Employees on createdByEmp.empCode = createdBy;
    modifiedByEmp    : Association to Employees on modifiedByEmp.empCode = modifiedBy;
    assignedToEmp    : Association to Employees on assignedToEmp.empCode = assignedToE;  
}
