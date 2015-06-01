/**
 * Created by robertk on 4/11/15.
 */
Ext.define('C2.view.c2.PublishEventsGrid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'C2.view.c2.C2Controller',
        'Ext.grid.column.Date'
    ],

    title: 'Publish Events',
    header: false,
    disableSelection: true,
    controller: 'c2',
    columns: [{
        text: 'ID',
        width: 100,
        dataIndex: 'id',
        renderer: 'c2EventIdRenderer'
    }, {
        text: 'Event Date',
        width: 180,
        dataIndex: 'eventDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'Status',
        width: 105,
        dataIndex: 'status',
        renderer: 'publishStatusRendererEvent'
    }, {
        text: 'C2 Request',
        flex: 1,
        dataIndex: 'c2Request',
        renderer: 'c2ReqResRenderer'
    }, {
        text: 'C2 Response',
        width: 500,
        dataIndex: 'c2Response',
        renderer: 'c2ReqResRenderer'
    }]
});