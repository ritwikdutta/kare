var map = d3.select('#graph').append('svg:svg');
var width = 900;
var height = 500;

map.attr('width', '100%')
   .attr('height', '100%');

map.text('Swag').select('#graph');

map.append('svg:circle');
map.selectAll('circle')
    .style('fill', 'blue')
    .attr('r', 30)
    .attr('cx', 30)
    .attr('cy', 30);

var nodes = d3.range(100).map(function(i) {
  return {index: i};
});

var force = d3.layout.force()
    .nodes(nodes)
    .size('100%', '100%')
    .on('tick', tick)
    .start();

function tick(e) {

  // Push different nodes in different directions for clustering.
  var k = 6 * e.alpha;
  nodes.forEach(function(o, i) {
    o.y += i & 1 ? k : -k;
    o.x += i & 2 ? k : -k;
  });

  node.attr("cx", function(d) { return d.x; })
      .attr("cy", function(d) { return d.y; });
}