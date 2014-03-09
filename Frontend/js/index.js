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
        var suggestions = new Bloodhound({
            datumTokenizer: function (d) {return Bloodhound.tokenizers.whitespace(d.url)},
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            limit: 5,
            local: results
        });
        suggestions.initialize();
        $(".typeahead").typeahead(null, {
            displayKey: "url",
            name: "users",
            source: suggestions.ttAdapter()

        });
    })
});