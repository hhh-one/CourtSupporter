const swiper = new Swiper('.swiper', {
	// Optional parameters
	direction: 'horizontal',
	slidesPerView: 1, // 한 번에 표시할 슬라이드 개수
	spaceBetween: 10, // 슬라이드 간 간격
	autoplay: {
		delay: 3000,
		disableOninteraction: false
	},
	loop: true,
	loopAdditionalSlides: 1,
});