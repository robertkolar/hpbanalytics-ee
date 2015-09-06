/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.model.report.Base', {
    extend: 'Ext.data.Model',

    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'}
    ],
    schema: {
        id: 'reportSchema',
        namespace: 'Report.model.report'  // generate auto entityName
    }
});