$(document).ready(function() {
	$("#notice > .container_head > .listbox > li > a").on("click", function(event) {
		// 기본 동작 비활성화
		event.preventDefault();

		var clicknum = $(this).parent().index();
		var containerHead = $(this).closest(".container_head");

		// 클릭한 항목의 부모에 "on" 클래스를 추가하고 형제 요소에서 제거합니다.
		$(this).parent().addClass("on").siblings().removeClass("on");

		// 모든 tabcontent 요소에서 "on" 클래스를 제거하고 clicknum 인덱스에 있는 요소에 추가합니다.
		containerHead.find(".tabcontent").removeClass("on").eq(clicknum).addClass("on");
	});
});