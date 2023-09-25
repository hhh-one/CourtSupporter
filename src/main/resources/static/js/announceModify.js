//summernote
$(document).ready(function() {
  $('#summernote').summernote({
  styleWithSpan: true,
  codeviewFilter: false, // 코드 보기 필터 비활성화
      codeviewIframeFilter: false, // 코드 보기 iframe 필터 비활성화

  //width: 500,
      height: 500, // 에디터 높이	      
      minHeight: 500, // 최소 높이
      maxHeight: null, // 최대 높이
      focus: true, // 에디터 로딩 후 포커스를 맞출지 여부
      lang: "ko-KR", // 한글 설정
      placeholder: '최대 2000자까지 쓸 수 있습니다', // placeholder 설정
      styleTags: [
          'p',  // 일반 문단 스타일 옵션
          {
              title: 'Blockquote',
              tag: 'blockquote',
              className: 'blockquote',
              value: 'blockquote',
          },  // 인용구 스타일 옵션
          'pre',  // 코드 단락 스타일 옵션
          {
              title: 'code_light',
              tag: 'pre',
              className: 'code_light',
              value: 'pre',
          },  // 밝은 코드 스타일 옵션
          {
              title: 'code_dark',
              tag: 'pre',
              className: 'code_dark',
              value: 'pre',
          },  // 어두운 코드 스타일 옵션
          'h1', 'h2', 'h3', 'h4', 'h5', 'h6',  // 제목 스타일 옵션
      ],

      toolbar: [
            ['style', ['style']],
            ['font', ['bold', 'underline', 'clear']],
            ['fontsize', ['fontsize']],
            ['color', ['color']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['table', ['table']],
            ['insert', ['link', 'picture', 'video']],
            ['view', ['fullscreen', 'codeview', 'help']]
      ],
      fontNames: ['Arial', 'Arial Black', 'Comic Sans MS', 'Courier New','맑은 고딕','궁서','굴림체','굴림','돋움체','바탕체'],
	  fontSizes: ['8','9','10','11','12','14','16','18','20','22','24','28','30','36','50','72']
	 
  });
});

$('.summernote').summernote('destroy');
var markupStr = $('.summernote').summernote('code');

// 에디터 내용을 텍스트로 가져오기
var editorText = $('.summernote').summernote('getText');
  
/////////////////////////////////////////////////////////////////////////
      
//조력자 선택 및 분류
$(document).ready(function() {

var mainSelect_a = ["조력자"];
var mainSelect_b = ["통번역인"];
var mainSelect_c = ["조정위원"];
var mainSelect_d = ["전문시밀위원"];
var mainSelect_e = ["상담위원"];

	var mainSelect_a_id = ["01"];
	var mainSelect_b_id = ["02"];
	var mainSelect_c_id = ["03"];
	var mainSelect_d_id = ["04"];
	var mainSelect_e_id = ["05"];
	
/*	var mainSelect_a_id = ["1010101"];
	var mainSelect_b_id = ["2020111"];
	var mainSelect_c_id = ["3030114"];
	var mainSelect_d_id = ["4040117"];
	var mainSelect_e_id = ["5050119"];*/
	
var mainSelect = document.getElementById("mainSelect");
var firstSelect = document.getElementById("firstSelect");
var secondSelect = document.getElementById("secondSelect");

$('#mainSelect').empty(); // 아래 option 삭제
 $('#mainSelect').append("<option value='' disabled selected style='display: none;'>대분류</option>"); 
 
  firstSelect.style.display = 'none';  	
  secondSelect.style.display = 'none';

$('#mainSelect').append("<option value='" + mainSelect_a_id + "'>" + mainSelect_a + "</option>");	
$('#mainSelect').append("<option value='" + mainSelect_b_id + "'>" + mainSelect_b + "</option>");
$('#mainSelect').append("<option value='" + mainSelect_c_id + "'>" + mainSelect_c + "</option>");
$('#mainSelect').append("<option value='" + mainSelect_d_id + "'>" + mainSelect_d + "</option>");
$('#mainSelect').append("<option value='" + mainSelect_e_id + "'>" + mainSelect_e + "</option>");

});	  


function selectChange() {

//중분류
var firstSelect_a = ["시가등(소송)", "경매", "공사비등", "측량", "문서/인영/필적", "번역/통역"];
var firstSelect_b = ["통역", "번역/통역", "통번역"];
var firstSelect_c = ["조정위원", "외부연계조정위원"];
var firstSelect_d = ["전문심리위원", "상임전문심리위원"];
var firstSelect_e = ["상담위원", "후견상담위원"];
var firstSelect_a_id = ["0101", "0102", "0103", "0104", "0105", "0106"];
var firstSelect_b_id = ["0201", "0202", "0203"];
var firstSelect_c_id = ["0301", "0302"];
var firstSelect_d_id = ["0401", "0402"];
var firstSelect_e_id = ["0501", "0502"];
var firstSelect = document.getElementById("firstSelect");
var secondSelect = document.getElementById("secondSelect");

$('#firstSelect').empty(); // 아래 option 삭제
$('#firstSelect').append("<option value='' disabled selected style='display: none;'>중분류</option>"); // 최상의 선택제목
firstSelect.style.display = 'inline-block';
//secondSelect.style.display = 'inline-block';

var supporterId = $('#mainSelect').val();
if (supporterId == "01") {	 
   for (i = 0; i < firstSelect_a.length; i++) {
      $('#firstSelect').append("<option value='" + firstSelect_a_id[i] + "'>" + firstSelect_a[i] + "</option>");
   }
} else if (supporterId == "02") {
   for (i = 0; i < firstSelect_b.length; i++) {
      $('#firstSelect').append("<option value='" + firstSelect_b_id[i] + "'>" + firstSelect_b[i] + "</option>");
   }
} else if (supporterId == "03") {
   for (i = 0; i < firstSelect_c.length; i++) {
      $('#firstSelect').append("<option value='" + firstSelect_c_id[i] + "'>" + firstSelect_c[i] + "</option>");
   }
} else if (supporterId == "04") {
   for (i = 0; i < firstSelect_d.length; i++) {
      $('#firstSelect').append("<option value='" + firstSelect_d_id[i] + "'>" + firstSelect_d[i] + "</option>");
   }
} else if (supporterId == "05") {
   for (i = 0; i < firstSelect_e.length; i++) {
      $('#firstSelect').append("<option value='" + firstSelect_e_id[i] + "'>" + firstSelect_e[i] + "</option>");
   }
}
}


function selectChange2() {
//소분류
var secondSelect_a = ["공사비등", "구조(안전진단)", "건축시공A", "건축시공B", "건축구조"];
var secondSelect_b = ["일반조정위원", "상임조정위원"];
var secondSelect_a_id = ["0103001", "0103002", "0103011", "0103012", "0103013"];
var secondSelect_b_id = ["0301001", "0301002"];
var firstSelect = document.getElementById("firstSelect");
var secondSelect = document.getElementById("secondSelect");
$('#secondSelect').empty(); // 아래 option 삭제
$('#secondSelect').append("<option value='' disabled selected style='display: none;'>소분류</option>"); // 최상의 선택제목
secondSelect.style.display = 'inline-block';

var str = $('#firstSelect').val();  

if (str == "0103") {
   secondSelect.style = 'none';    
   for (i = 0; i < secondSelect_a.length; i++) {
      $('#secondSelect').append("<option value='" + secondSelect_a_id[i] + "'>" + secondSelect_a[i] + "</option>");
   }
} else if (str == "0301") {
   secondSelect.style = 'none';     
   for (i = 0; i < secondSelect_b.length; i++) {
      $('#secondSelect').append("<option value='" + secondSelect_b_id[i] + "'>" + secondSelect_b[i] + "</option>");
   }
} else {
   secondSelect.style.display = 'none';
}
}


/* 조력자 분류 유효성 검사 */
/*$('#firstSelect').on('change', function() {
  $("#msg1").text(""); // 선택 값이 변경되면 메시지 삭제
  $("#msg2").text("");
  $('#firstSelect').focus().css('border', '');
});
$('#secondSelect').on('change', function() {
  $("#msg2").text(""); // 선택 값이 변경되면 메시지 삭제
  $('#secondSelect').focus().css('border', '');
});

let firstSelect = $('#firstSelect').val();
if(!firstSelect){
  $("#msg1").text("필수 선택 항목입니다.");
  $('#firstSelect').focus().css('border', '1.8px solid red');
  return false;
}

let secondSelect = $('#secondSelect').val();
if (firstSelect == '0103' || firstSelect == '0301') {
if (!secondSelect) {
  $("#msg2").text("필수 선택 항목입니다.");
  $('#secondSelect').focus().css('border', '1.8px solid red');
  return false;
}
}*/
/////////////////////////////////////////////////////////////////////////



// 파일 입력 필드
const fileInput = document.getElementById('files');

// 파일 이름을 나타낼 영역
const fileNamesDisplay = document.getElementById('fileNames');

// 파일 입력 필드의 변경 이벤트 리스너 추가
fileInput.addEventListener('change', () => {
	// 선택된 파일 목록 가져오기
	const selectedFiles = fileInput.files;

	// 파일 이름을 나타내는 문자열 초기화
	let fileNames = '';

	// 선택된 각 파일의 이름을 문자열에 추가
	for (let i = 0; i < selectedFiles.length; i++) {
		fileNames += selectedFiles[i].name + '<br>';
	}

	// 파일 이름을 나타낼 영역 업데이트
	fileNamesDisplay.innerHTML = fileNames;
});
/////////////////////////////////////////////////////////////////////////

//파일 첨부
var contentDetail = document.getElementById("contentDetail");
contentDetail.innerHTML = contentDetail.innerHTML.replace(/\n/g, "<br>");

//파일 첨부 및 삭제
$(document).ready(function() {
	$("a[name='file-delete']").on("click", function(e) {
		e.preventDefault();
		deleteFile($(this));
	})
});
	  
function addFile(){ // id='fileList'
	
	 var str = "<div><input type='file' name='file' class='sel' multiple >";
	    str += "<a href='#this' name='file-delete' class='delete'>삭제</a></div>";
	   
	$("#fileList").append(str);
	$("a[name='file-delete']").on("click", function(e) {
		e.preventDefault();
		deleteFile($(this));
	});
}

function deleteFile(obj) {
	obj.parent().remove();		
}
/////////////////////////////////////////////////////////////////////////

 //작성취소 - 목록으로 돌아가기
let listBtn = document.getElementById("listBtn");
listBtn.onclick = () => {
	event.preventDefault();
	window.location.href = "/announce/announceList";
}

/////////////////////////////////////////////////////////////////////////

//등록 전 확인
let modifyBtn = document.getElementById("modifyBtn");  
  modifyBtn.onclick = () => {
	  
    if(modifyForm.announce_title.value == ""){
      alert("제목을 입력하세요.");
      modifyForm.announce_title.focus();    
      return false;      
	} else if(modifyForm.announce_start_date.value == "") {
      alert("모집 시작일을 선택하세요.");
      modifyForm.announce_start_date.focus();      
      return false;
    } else if(modifyForm.announce_end_date.value == "") {
      alert("모집 종료일을 선택하세요.");
      modifyForm.announce_end_date.focus();      
      return false;
    } else if(modifyForm.trial_fcltt_proper_num.value == "") {
      alert("조력자 선택은 필수입니다.");
      modifyForm.trial_fcltt_proper_num.focus();
      return false;    
    } else if(modifyForm.announce_recruit_num.value == "") {
      alert("모집 인원을 입력하세요.");
      modifyForm.announce_recruit_num.focus();
      return false;
    } else if(modifyForm.announce_content.value == "") {
      alert("내용을 입력하세요.");
      modifyForm.announce_content.focus();      
      return false;
    } else {
      alert("정상적으로 등록되었습니다.");     
      return true;
    }
  }    
 