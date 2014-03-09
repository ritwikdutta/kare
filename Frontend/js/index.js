$(document).ready(function() {
function search(repo) {
    $.get("/recs?repo=" + repo , function(data) {
        console.log(repo);
        $("#append").append(data);
    });
}

$("#searchbtn").click(function () {
    search($(this).val());
});

$(".textbox").keypress(function (e) {
    if (e.which == 13) {
        search($(this).val());
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
    console.log('users ok');
});

var repos = new Bloodhound({
    datumTokenizer: function(d) {return Bloodhound.tokenizers.whitespace(d.value)},
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    limit: 15,
    remote: {
        url:'repos?q=%QUERY',
        filter: function(data) {
            return $.map(data.results, function(unit) {
               return {
                   value: unit.command
               } 
            });
        }
    }
    
});
repos.initialize().done(function(){
    console.log("repo ok");
});
var suggestions = function (query, cb) {
    var ret;
    //if query contains / it is a repo search
    
    if (query.indexOf('/') > -1 ) {
        console.log(query.substring(query.indexOf('/')+1));
        repos.get(query.substring(0,query.indexOf('/')+1), function(data) {
            
            if (data.length > 0){
                console.log('sw');
                ret = data;  
                cb(data);
            }
            
        });
        
        
    } else {
        users.get(query, function(data){
            if (data.length > 0) {
                cb(data);
            }
        })
    }
}

$('.textbox').typeahead(null, {
    displayKey: 'value',
    source: suggestions
});

$(".textbox").keypress(function (e) {
    if (e.which == 13) {
        console.log('swag');
        $(this).change();
    }
    
   
    
});

/*
$('.textbox').typeahead(null, {
    displayKey: 'value',
    source: repos.ttAdapter()
});
*/
});

