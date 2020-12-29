$(document).ready(function()
{
	var currentPage = 1;
	loadData(currentPage);
	
	$(window).scroll(function()
	{
		if($(window).scrollTop() + $(window).height() == $(document).height())
		{
			currentPage += 1;
			loadData(currentPage);
			console.log("in on scroll");
		}
	});
	
	function loadData(currentPage)
	{
		console.log("in load data");
		$.ajax({
			type : "POST",
			url : "/user/home/getData",
			data:{
				page : currentPage,
				pageSize : 5
			},
			success : function(result)
			{
				alert("success " + result);
			},
			error : function(e) {
				alert("Error!")
				console.log("ERROR: ", e.responseText);
			}
		});
	}
});