function search() {
    'use strict';       
}

$("#searchbtn").click(function (e) {
    search();
});

$('.textbox').keypress(function (e) {
    if (e.which === 13) {
        search();
    }
});