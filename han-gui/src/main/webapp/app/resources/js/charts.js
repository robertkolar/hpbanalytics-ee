/**
 * Created by robertk on 21.9.2015.
 */
var HpbChart = (function() {
    var my = {};

    my.clearAllCharts = function() {
        d3.selectAll("svg").remove();
    };

    my.createBarChart = function(items, timeFrame, title, divEl) {
        var margin = {top: 20, right: 50, bottom: 20, left: 60},
            width = 1210 - margin.left - margin.right,
            height = 200 - margin.top - margin.bottom;

        var x = d3.scaleTime().range([0, width]);
        var y = d3.scaleLinear().range([height, 0]);

        var xAxis = d3.axisBottom(x);

        var yAxis = d3.axisLeft(y)
            .tickFormat(d3.format("d"))
            .ticks(4);

        var tooltip = d3.select("body").append("div")
            .attr("class", "d3-tooltip")
            .style("opacity", 0);

        var svg = d3.select("#" + divEl).append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        x.domain([timeFrame.fromDate, timeFrame.toDate]);

        y.domain([0, d3.max(items, function(d) {return Math.abs(d.v);})]);

        svg.append("g")
            .attr("class", "x d3-axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis);

        svg.append("g")
            .attr("class", "y d3-axis")
            .call(yAxis)
            .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", ".71em")
            .attr("fill", "#000")
            .style("text-anchor", "end")
            .text(title);

        svg.selectAll(".bar")
            .data(items)
            .enter()
            .append("rect")
            .attr("class", "d3-bar")
            .attr("x", function(d) {return x(d.periodDate);})
            .attr("width", 8)
            .attr("y", function(d) {return y(Math.abs(d.v));})
            .attr("height", function(d) {return height - y(Math.abs(d.v));})
            .style("fill", function(d) { return d.v >= 0 ? 'green' : 'red'; })
            .on("mouseover", function(d) {
                tooltip.transition().duration(200)
                    .style("opacity", .9);
                tooltip.html(Ext.Date.format(new Date(d.periodDate), 'm/d/Y') + '<br/><span style="color: ' + (d.v >= 0 ? 'green' : 'red') + '">' + d.v + '</span>')
                    .style("left", (d3.event.pageX) + "px")
                    .style("top", (d3.event.pageY - 28) + "px");
            })
            .on("mouseout", function(d) {
                tooltip.transition().duration(500)
                    .style("opacity", 0);
            });
    };

    my.createCompoundBarChart = function(items, timeFrame, title, divEl) {
        var margin = {top: 20, right: 50, bottom: 20, left: 60},
            width = 1210 - margin.left - margin.right,
            height = 200 - margin.top - margin.bottom;

        var x = d3.scaleTime().range([0, width]);
        var y = d3.scaleLinear().range([height, 0]);

        var color = d3.scaleOrdinal()
            .range(["#339900", "#CC0000"]);

        color.domain(['v1', 'v2']);

        var xAxis = d3.axisBottom(x);

        var yAxis = d3.axisLeft(y)
            .tickFormat(d3.format("d"))
            .ticks(4);

        var tooltip = d3.select("body").append("div")
            .attr("class", "d3-tooltip")
            .style("opacity", 0)
            .style("height", '60px');

        var svg = d3.select("#" + divEl).append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        items.forEach(function(d) {
            var y0 = 0;
            d.parts = color.domain().map(function(name) { return {name: name, y0: y0, y1: y0 += +d[name], periodDate: d.periodDate}; });
            d.total = d.parts[d.parts.length - 1].y1;
        });

        x.domain([timeFrame.fromDate, timeFrame.toDate]);

        y.domain([0, d3.max(items, function(d) {return d.total})]);

        svg.append("g")
            .attr("class", "x d3-axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis);

        svg.append("g")
            .attr("class", "y d3-axis")
            .call(yAxis)
            .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", ".71em")
            .attr("fill", "#000")
            .style("text-anchor", "end")
            .text(title);

        var bar = svg.selectAll(".bar")
            .data(items)
            .enter().append("g")
            .attr("class", "g d3-bar")
            .attr("x", function(d) {return x(d.periodDate);})
            .on("mouseover", function(d) {
                tooltip.transition().duration(200)
                    .style("opacity", .9);
                tooltip
                    .html(Ext.Date.format(new Date(d.periodDate), 'm/d/Y') + '<br/>' +
                        '<span style="color: green;">v1=' + d.v1 + '</span><br/>' +
                        '<span style="color: red;">v2=' + d.v2 + '</span><br/>' + 'tot=' + d.total)
                    .style("left", (d3.event.pageX) + "px")
                    .style("top", (d3.event.pageY - 28) + "px");
            })
            .on("mouseout", function(d) {
                tooltip.transition().duration(500)
                    .style("opacity", 0);
            });

        bar.selectAll("rect")
            .data(function(d) { return d.parts; })
            .enter().append("rect")
            .attr("x", function(d) {return x(d.periodDate)})
            .attr("width", 8)
            .attr("y", function(d) { return y(d.y1); })
            .attr("height", function(d) { return y(d.y0) - y(d.y1); })
            .style("fill", function(d) { return color(d.name); });
    };

    my.ceateLineChart = function(items, timeFrame, title, divEl) {
        var margin = {top: 20, right: 50, bottom: 20, left: 60},
            width = 1210 - margin.left - margin.right,
            height = 200 - margin.top - margin.bottom;

        var x = d3.scaleTime().range([0, width]);
        var y = d3.scaleLinear().range([height, 0]);

        var xAxis = d3.axisBottom(x);

        var yAxis = d3.axisLeft(y)
            .tickFormat(d3.format("d"))
            .ticks(4);

        var line = d3.line()
            .x(function(d) { return x(d.periodDate); })
            .y(function(d) { return y(d.v); });

        var svg = d3.select("#" + divEl).append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        x.domain([timeFrame.fromDate, timeFrame.toDate]);

        y.domain(d3.extent(items, function(d) { return d.v; }));

        svg.append("g")
            .attr("class", "x d3-axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis);

        svg.append("g")
            .attr("class", "y d3-axis")
            .call(yAxis)
            .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", ".71em")
            .attr("fill", "#000")
            .style("text-anchor", "end")
            .text(title);

        svg.append("path")
            .attr("class", "d3-line")
            .attr("d", line(items));
    };
    return my;
}());