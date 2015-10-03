/**
 * Created by robertk on 4/11/15.
 */
Ext.define('C2.view.c2.PublishEventsGrid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Ext.grid.column.Date'
    ],

    title: 'Publish Events',
    header: false,
    disableSelection: true,
    columns: {
        defaults: {
            style: 'background-color: #157fcc; color: black;'
        },
        items: [{
            text: 'ID',
            width: 100,
            dataIndex: 'id',
            renderer: function(val, metadata, record) {
                return record.data['id'] + '/' + record.data['c2SignalDbId'];
            }
        }, {
            text: 'Event Date',
            width: 180,
            dataIndex: 'eventDate',
            xtype: 'datecolumn',
            format: 'm/d/Y H:i:s.u'
        }, {
            text: 'Status',
            width: 80,
            dataIndex: 'status',
            renderer: function(val, metadata, record) {
                metadata.style = 'background-color: ' + C2.common.Definitions.getPublishStatusColor(val) + '; color: white;';
                return val.toLowerCase();;
            }
        }, {
            text: 'C2 Request',
            flex: 1,
            dataIndex: 'c2Request',
            renderer: function(val, metadata, record) {
                metadata.style = 'white-space: normal; word-wrap: break-word;';
                return Ext.String.htmlEncode(val);
            }
        }, {
            text: 'C2 Response',
            width: 500,
            dataIndex: 'c2Response',
            renderer: function(val, metadata, record) {
                metadata.style = 'white-space: normal; word-wrap: break-word;';
                return Ext.String.htmlEncode(val);
            }
        }]
    }
});