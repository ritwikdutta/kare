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
/*
$(".textbox").on("input", function (e) {
    var value = $(this).val();
   $.getJSON("/users?q=" + value, function (data) {
        var results = data.results;
        console.log(results);
        
        suggestions.initialize();
        $(".typeahead").typeahead(null, {
            
            source: suggestions.ttAdapter()

        });
    })

});
*/

var users = new Bloodhound({
    datumTokenizer: function (d) {return Bloodhound.tokenizers.whitespace(d.value)},
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    limit: 5,
    remote: {
        url:'users?q=%QUERY',
        filter: function(data) {
            return $.map(data.results, function(unit) {
                return {
                    value: unit.url.substring(1) + '/'
                }
            });
        }
    }
    
});

users.initialize().done(function(){
    console.log('ok');
});

var suggestions = new Bloodhound({
    
})

$('.textbox').typeahead(null, {
    displayKey: 'value',
    source: suggestions.ttAdapter()
})

