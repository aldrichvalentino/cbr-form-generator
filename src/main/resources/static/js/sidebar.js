$(document).ready(() => {
    // add active class according to current page
    $('#menu-' + location.pathname.split("/")[1]).addClass('active');
});