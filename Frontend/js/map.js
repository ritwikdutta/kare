var map = d3.select('#graph').append('svg:svg');
var width = 900;
var height = 500;

map.attr('width', width)
   .attr('height', height);

map.text('Swag').select('#graph');

map.append('svg:circle');
map.selectAll('circle')
    .style('fill', 'blue')
    .attr('r', 30)
    .attr('cx', 30)
    .attr('cy', 30);