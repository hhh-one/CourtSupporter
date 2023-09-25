//sns 공유 박스 유무
let snsBtn = document.getElementById("snsBtn");
let snsBox = document.getElementById("snsBox");
snsBtn.onclick = function() {
	event.preventDefault();
	if(snsBox.classList.contains("on")) {
		snsBox.classList.remove("on");
		snsBox.style.display = "none";
	} else {
		snsBox.classList.add("on");			
		snsBox.style.display = "block";
	}		
}
	
//공유버튼 링크
function shareTwitter() {
   let txt = "모집공고";
   let url = ""; 
   window.open("https://twitter.com/intent/tweet?text=" + txt +"&url=" + url);
}

function shareFacebook() {	   
   let url = ""; 
   window.open("http://www.facebook.com/sharer/sharer.php?u=" +"&url=" + url);
}


function printPageArea(areaID){
    var printContent = document.getElementById(areaID).innerHTML;	    
    var printWindow = window.open('', '_blank');
    printWindow.document.open();
    printWindow.document.write('<html><head><title>Print</title></head><body>');
    printWindow.document.write(printContent);
    printWindow.document.write('</body></html>');
    printWindow.document.close();
    printWindow.print();
    printWindow.close()
    
}

//화면 확대 축소 기능
let now = 100;

function zooms() {
	document.body.style.zoom = now + "%";		
	if(now == 70) {
		alert("더 이상 축소할 수 없습니다.");
	}
	if(now == 200){
		alert("더 이상 확대할 수 없습니다.");
	}		
}

function zoomIn() {
	now = now + 10;
	if(now >= 200) {
		now = 200;
	}
	zooms();
}
function zoomReset() {
	now = 100;
	zooms();
}
function zoomOut() {
	now = now - 10;
	if(now <= 70) {
		now = 70;
	}
	zooms();
}
	
//모집기간 지난 경우 신청 버튼 숨기기
var announceEnddate = document.getElementById("endDate");
var enddate = announceEnddate.textContent;

function getToday() {
	var today = new Date();
	//console.log(today);
	var year = today.getFullYear();
    var month = String(today.getMonth() + 1).padStart(2, "0");
    var day = String(today.getDate()).padStart(2, "0");
    return year + "-" + month + "-" + day;
}

function hideDate() {
	var currentDate = getToday();
	if( currentDate > enddate ) {
		document.getElementById("applyBtn").style.display = "none";
	}
}	
window.onload = hideDate;


//작성취소 - 목록으로 돌아가기
let listBtn = document.getElementById("listBtn");
listBtn.onclick = (event) => {
  event.preventDefault();
  window.location.href = "/announce/announceList";
}

// 삭제 기능
let deleteBtn = document.getElementById("deleteBtn");
deleteBtn.onclick = (event) => {		
  event.preventDefault();		
  if(confirm("삭제하시겠습니까?")){
    document.actionForm.action = "deleteForm"; //action변경 
    document.actionForm.submit();			
  }
}	