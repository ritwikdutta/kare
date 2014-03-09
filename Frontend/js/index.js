function search() {

}

$("#searchbtn").click(function () {
    search();
});

$(".textbox").keypress(function (e) {
    if (e.which == 13) {
        search();
    }
});

$(".textbox").on("input", function (e) {
    var value = $(this).val();
    $.getJSON("/users?q=" + value, function (data) {
        var results = data.results;
        console.log(results[0].prefix);
    })
});