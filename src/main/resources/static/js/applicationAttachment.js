/* 파일 첨부 추가 */
/* 사업자 등록증 */
$(document).ready(function() {
	var idx = 1;
	$(document).on("click", ".CoFilePlusBtn1", function() {
		var str = "";
		str += '<div class="file-container">';
		str += '<label for="license-file-input-' + idx + '" class="file-label">';
		str += '<input type="hidden" class="fileAdd" style="border: none;" value="' + idx + '">';
		str += '<input type="file" id="license-file-input-' + idx + '" name="co_license" style="display: none;">';
		str += '<span class="fileAddBtn licenseAddBtn">파일선택</span>';
		str += '</label>';
		str += '<input id="file-upload-name2" class="lisence-upload-name' + idx + ' name_box license_name" value="" disabled="disabled">';
		str += '<input type="button" class="fileDeleteBtn" value="삭제" style="color: #fff; width: 80px;display: none">';
		str += '</div>';
		
		$("#CoFileAddList1").append(str);
		idx++;
	});
	
})
/* 업무 관련 등록 신고 현황 */
$(document).ready(function() {
	var idx = 1;
	$(document).on("click", ".CoFilePlusBtn2", function() {
		var str = "";
		str += '<div class="file-container">';
		str += '<label for="report-file-input-' + idx + '" class="file-label">';
		str += '<input type="hidden" class="fileAdd" style="border: none;" value="' + idx + '">';
		str += '<input type="file" id="report-file-input-' + idx + '" name="co_report" style="display: none;">';
		str += '<span class="fileAddBtn reportAddBtn">파일선택</span>';
		str += '</label>';
		str += '<input id="file-upload-name2" class="report-upload-name' + idx + ' name_box report_name" value="" disabled="disabled">';
		str += '<input type="button" class="fileDeleteBtn" value="삭제" style="color: #fff; width: 80px;display: none">';
		str += '</div>';
		
		$("#CoFileAddList2").append(str);
		idx++;
	});
	
})

/* 납세증명확인서 */
$(document).ready(function() {
	var idx = 1;
	$(document).on("click", ".CoFilePlusBtn3", function() {
		var str = "";
		str += '<div class="file-container">';
		str += '<label for="tax-file-input-' + idx + '" class="file-label">';
		str += '<input type="hidden" class="fileAdd" style="border: none;" value="' + idx + '">';
		str += '<input type="file" id="tax-file-input-' + idx + '" name="co_tax" style="display: none;">';
		str += '<span class="fileAddBtn taxfileAddBtn">파일선택</span>';
		str += '</label>';
		str += '<input id="file-upload-name2" class="tax-upload-name' + idx + ' name_box taxfile_name" value="" disabled="disabled">';
		str += '<input type="button" class="fileDeleteBtn" value="삭제" style="color: #fff; width: 80px;display: none">';
		str += '</div>';
		
		$("#CoFileAddList3").append(str);
		idx++;
	});
	
})

/* 이력서 */
$(document).ready(function() {
	var resumeContainer = $("#resume_container");
	if (resumeContainer.children().length === 0) {
		
		var str = "";
		str += '<div>';
		str += '<label for="file-input" class="file-label">';
		str += '<input type="hidden" class="uuid">';
		str += '<input type="hidden" class="type">';
		str += '<input type="file" id="file-input" name="pe_resume" style="display: none;">';
		str += '<span id="resume_btn" class="fileAddBtn addFile">파일 선택</span>';		
		str += '</label>';
		str += '<input id="resume_name" class="upload-name" disabled="disabled" style="width: 166px;display: none;">';
		str += '<input type="button" class="PeFileDeleteBtn1" value="삭제" style="display: none;">';
		str += '</div>';					
		
		resumeContainer.append(str);
	} else {
		$('#resume_btn').css('display', 'none');
	}
})
/* 학력 정보 */
$(document).ready(function() {
	var idx = 1;
	$(document).on("click", ".PeFilePlusBtn2", function() {
		var str = "";
		str += '<div class="file-container">';
		str += '<label for="edu-file-input-' + idx + '" class="file-label">';
		str += '<input type="hidden" class="fileAdd" style="border: none;" value="' + idx + '">';
		str += '<input type="file" id="edu-file-input-' + idx + '" name="pe_edu" style="display: none;">';
		str += '<span class="fileAddBtn">파일선택</span>';
		str += '</label>';
		str += '<input id="file-upload-name2" class="edu-upload-name' + idx + ' name_box" value="" disabled="disabled">';
		str += '<input type="button" class="fileDeleteBtn" value="삭제" style="color: #fff; width: 80px;display: none;">';
		str += '</div>';
		
		$("#PeFileAddList2").append(str);
		idx++;
	});
	
})

/* 경력 정보 */
$(document).ready(function() {
	var idx = 1;
	$(document).on("click", ".PeFilePlusBtn3", function() {
		var str = "";
		str += '<div class="file-container">';
		str += '<label for="work-file-input-' + idx + '" class="file-label">';
		str += '<input type="hidden" class="fileAdd" style="border: none;" value="' + idx + '">';
		str += '<input type="file" id="work-file-input-' + idx + '" name="pe_work" style="display: none;">';
		str += '<span class="fileAddBtn">파일선택</span>';
		str += '</label>';
		str += '<input id="file-upload-name2" class="work-upload-name' + idx + ' name_box" value="" disabled="disabled">';
		str += '<input type="button" class="fileDeleteBtn" value="삭제" style="color: #fff; width: 80px;display: none">';
		str += '</div>';
		
		$("#PeFileAddList3").append(str);
		idx++;
	});
	
})

/* 자격증 정보 */
$(document).ready(function() {
	var idx = 1;
	$(document).on("click", ".PeFilePlusBtn4", function() {
		var str = "";
		str += '<div class="file-container">';
		str += '<label for="cert-file-input-' + idx + '" class="file-label">';
		str += '<input type="hidden" class="fileAdd" style="border: none;" value="' + idx + '">';
		str += '<input type="file" id="cert-file-input-' + idx + '" name="pe_cert" style="display: none;">';
		str += '<span class="fileAddBtn">파일선택</span>';
		str += '</label>';
		str += '<input id="file-upload-name2" class="cert-upload-name' + idx + ' name_box" value="" disabled="disabled">';
		str += '<input type="button" class="fileDeleteBtn" value="삭제" style="color: #fff; width: 80px;display: none">';
		str += '</div>';
		
		$("#PeFileAddList4").append(str);
		idx++;
	});
	
})

/* 기타 증빙서류 */
$(document).ready(function() {
	var idx = 1;
	$(document).on("click", ".OtFilePlusBtn", function() {
		var str = "";
		str += '<div class="file-container">';
		str += '<label for="ot-file-input-' + idx + '" class="file-label">';
		str += '<input type="hidden" class="fileAdd" style="border: none;" value="' + idx + '">';
		str += '<input type="file" id="ot-file-input-' + idx + '" name="ot_file" style="display: none;">';
		str += '<span class="fileAddBtn">파일선택</span>';
		str += '</label>';
		str += '<input id="file-upload-name2" class="ot_upload-name' + idx + ' name_box" value="" disabled="disabled">';
		str += '<input type="button" class="fileDeleteBtn" value="삭제" style="color: #fff; width: 80px;display: none">';
		str += '</div>';
		
		$("#OtFileAddList").append(str);
		idx++;
	});
	
})

/* 파일 수정 & 유효성 검사 */
window.onload = function() {
	$("#attachmentForm").on('submit', function(e) {
		e.preventDefault();

		var values = $('.license_name').map(function() {
						  return $(this).val();
						}).get();
		var allEmpty = values.every(function(value) {
						  return value === "";
						});
		var values2 = $('.report_name').map(function() {
						  return $(this).val();
						}).get();
		var allEmpty2 = values2.every(function(value) {
						  return value === "";
						});
		var values3 = $('.taxfile_name').map(function() {
						  return $(this).val();
						}).get();
		var allEmpty3 = values3.every(function(value) {
						  return value === "";
						});
		
		$('.CoFilePlusBtn1, .licenseAddBtn').on('click', function() {
			$("#msg1").text("");
		});
		
		$('.CoFilePlusBtn2, .reportAddBtn').on('click', function() {
			$("#msg2").text("");
		});
		
		$('.CoFilePlusBtn3, .taxfileAddBtn').on('click', function() {
			$("#msg3").text("");
		});

		if ($('#businesslicenseFile').children().length === 0 && (values.length === 0 || allEmpty)) {
		  $("#msg1").text("사업자 등록증 첨부는 필수입니다").focus();
			return false;
		} else if ($('#businessreportFile').children().length === 0 && (values2.length === 0 || allEmpty2)) {
		  $("#msg2").text("업무관련 등록 신고현황 첨부는 필수입니다").focus();
			return false;
		} else if ($('#taxconfirmFile').children().length === 0 && (values3.length === 0 || allEmpty3)) {
		  $("#msg3").text("납세증명확인서 첨부는 필수입니다").focus();
			return false;
		}
		
		var uuids = [];
		var types = [];
		const elements1 = document.getElementsByClassName("uuid");
		const elements2 = document.getElementsByClassName("type");
		for(var i = 0; i < elements1.length; i++) {
			uuids.push(elements1[i].value);
			types.push(elements2[i].value);
		}
		
		var detail = document.getElementById('aplcn_dtls_proper_num').value;
		$.ajax({
			url: "../application/attachmentFileModify",
			method: "POST",
			contentType: "application/json",
			data: JSON.stringify({ uuids: uuids, types: types, detail: detail }),
			success: function(data) {
				console.log(data)
				document.attachmentForm.submit();
			},
			error: function(error) {
				console.error(error);
			}
		});
		
	})
}

/* 파일 첨부 추가 삭제 */
$(document).ready(function() {
	$(document).on("click", ".fileDeleteBtn, .fileDeleteBtn2", function() {
		$(this).parent().remove();
	});
	
})
$(document).ready(function() {
	$(document).on("change", "#file-input", function(){
		var fileInput = $(this);
	  	var fileName = fileInput.val().split('\\').pop();
	  	fileInput.parent().next().val(fileName);
		$(this).parent().next().css('display', '');
	});
	
	$(document).on("click", ".PeFileDeleteBtn1", function() {
		$(this).parent().remove();

		var str = "";
		str += '<div>';
		str += '<label for="file-input" class="file-label">';
		str += '<input type="hidden" class="uuid_type">';
		str += '<input type="file" id="file-input" name="pe_resume" style="display: none;">';
		str += '<span id="resume_btn" class="fileAddBtn" style="width: 130px;">파일 선택</span>';	
		str += '</label>';
		str += '<input id="resume_name" class="upload-name" disabled="disabled" style="width: 166px;display: none;">';
		str += '<input type="button" class="PeFileDeleteBtn1" value="삭제" style="color: #fff; width: 80px;display: none;">';
		str += '</div>';
		
		$("#resume_container").append(str);
	});
	
})
/* 파일 업로드 */
$(document).ready(function() {

   $(document).on("change", 'input[type=file]', function() {
	   
  	var fileInput = $(this);
  	var fileName = fileInput.val().split('\\').pop();
  	fileInput.parent().next().val(fileName);
  	
  	$(this).next().css('display', 'none');
  	$(this).parent().next().next().css({'display': '', 'background-color' : 'rgb(11, 38, 110)'});
  });
  
});

/* 이전 버튼 */
function returnPage() {
	var announce = document.getElementById('announce_proper_num').value;
	var fcltt_num = document.getElementById('trial_fcltt_proper_num').value;
	var detail = document.getElementById('aplcn_dtls_proper_num').value
	location.href = '/application/applicationCertificate?announce=' + announce + "&fcltt_num=" + fcltt_num + "&detail=" + detail;
}