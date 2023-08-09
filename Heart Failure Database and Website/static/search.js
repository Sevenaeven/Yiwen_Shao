Search = function (){

    var input = document.getElementById('importsubject');

    if(input != null){
         $.ajax({
            type: "GET",
            url: "/importsub",
            data: {
                input : input.value
            },
            success: function(data) {
                  var newContent = $(data).find('#searchTable').html();
                  $('#searchTable').html(newContent);
        },
            error: function() {
                alert("Connection failed.")
        }
     })
    }
}


Add = function() {
    var data = {}
    data['row_id'] = $('#row_id').val()
    data['subject_id'] = $('#subject_id').val()
    data['gender'] = $('#gender').val()
    data['dob'] = $('#dob').val()
    data['dod'] = $('#dod').val()
    data['dod_hosp'] = $('#dod_hosp').val()
    data['dod_ssn'] = $('#dod_ssn').val()
    data['expire_flag'] = $('#expire_flag').val()

    $.ajax({
        type: "GET",
        url: "/add/patient",
        data: data,
        success: function(data) {
             alert("Adding Information Successfully.")
    },
        error: function() {
            alert("Connection failed.")
    }

 })
 }


Update = function () {
        var data = {}
        data['subject_id'] = $('#subject_id').val()
        data['dod'] = $('#dod').val()
        data['dod_hosp'] = $('#dod_hosp').val()
        data['dod_ssn'] = $('#dod_ssn').val()
        data['expire_flag'] = $('#expire_flag').val()

        $.ajax({
            type: "GET",
            url: "/update/patient",
            data: data,
            success: function(data) {
                 alert("Updating information successfully.")
        },
            error: function() {
                alert("Connection failed.")
        }

     })
 }


Delete = function () {
        var data = {}
        data['subject_id'] = $('#subject_id').val()
        $.ajax({
            type: "GET",
            url: "/delete/patient",
            data: data,
            success: function(data) {
                 alert("Deleting information successfully");
        },
            error: function() {
                alert("Connection failed.")
        }

     })
 }
