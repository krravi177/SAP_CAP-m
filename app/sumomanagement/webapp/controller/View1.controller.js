sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/model/odata/v4/ODataModel"
], function (Controller, ODataModel) {
    "use strict";
 
    return Controller.extend("sumomanagement.controller.View1", {
 
        // Called when the controller is initialized
        onInit: function () {
            // Create an OData Model pointing to the backend service
            var oModel= this.getView().getModel();
 
 
            // Set the OData model to the view
            this.getView().setModel(oModel);
 
            // Optional: You can add other initializations if needed
        },
 
        // Handle filter changes when the user changes filters
        onFilterChange: function (oEvent) {
            var oFilterBar = this.byId("smartFilterBar");
            var oFilters = oFilterBar.getFilters();
            // Handle the filter logic or trigger re-binding here if required
        },
 
        // Handle any other events like sort, etc.
        onSortChange: function (oEvent) {
        // Handle the sort dialog if implemented
        }
    });
});
 