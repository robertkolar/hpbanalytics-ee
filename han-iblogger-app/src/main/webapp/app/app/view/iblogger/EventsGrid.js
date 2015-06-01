/**
 * Created by robertk on 4/17/15.
 */
Ext.define('IbLogger.view.iblogger.EventsGrid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Date'
    ],

    controller: 'iblogger',
    disableSelection: true,
    header: false,
    columns: [{
        text: 'ID',
        width: 100,
        dataIndex: 'id',
        renderer: 'ibEventIdRenderer'
    }, {
        text: 'Event Date',
        width: 180,
        dataIndex: 'eventDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'Status',
        width: 120,
        dataIndex: 'status',
        renderer: 'statusRendererEvent'
    }, {
        text: 'Upd Price',
        width: 120,
        dataIndex: 'updatePrice'
    }, {
        text: 'Fill Prc',
        flex: 1,
        dataIndex: 'fillPrice'
    }]
});