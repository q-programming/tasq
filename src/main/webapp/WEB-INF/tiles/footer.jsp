<script>
$(function() {
	$('.a-tooltip').tooltip();
});
//add slidedown animation to dropdown menus
$('.dropdown').on('show.bs.dropdown', function(e){
  $(this).find('.dropdown-menu').first().stop(true, true).slideDown();
});

//add slideup animation to dropdown menus
$('.dropdown').on('hide.bs.dropdown', function(e){
  $(this).find('.dropdown-menu').first().stop(true, true).slideUp();
});
</script>