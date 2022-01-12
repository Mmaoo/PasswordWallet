//$(document).ready(function () {
//
//    $("#search-form").submit(function (event) {
//
//        //stop submit the form, we will post it manually.
//        event.preventDefault();
//
//        fire_ajax_submit();
//
//    });
//
//});

function ShowPass(id, shared) {

    var search = {};
    search["password_id"] = id;
    search["shared"] = shared;

    if(!($('#pass_checkbox_'+id).prop('checked'))){
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/api/passwords/decrypt",
            data: JSON.stringify(search),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                $('#pass_show_'+id).html(data.enc_pass);
                $('#pass_checkbox_'+id).prop('checked',true);
                $('#pass_button_'+id).html("Schowaj hasło");
                console.log("SUCCESS : "/*, data*/);
            },
            error: function (e) {
                console.log("ERROR : ", e);
                var json = e.responseText;
                $('#pass_show_'+id).html(json);
                $('#pass_show_'+id).html("&lt;Private&gt;");
                $('#pass_button_'+id).html("Pokaż hasło");
                $('#pass_checkbox_'+id).prop('checked',false);
            }
        });
    }else{
        $('#pass_show_'+id).html("&lt;Private&gt;");
        $('#pass_button_'+id).html("Pokaż hasło");
        $('#pass_checkbox_'+id).prop('checked',false);
    }
}

function showShareBox(id){
    var email = prompt("Podaj e-mail osoby której chcesz udostępnić hasło");
    if(email != null && email != ""){
        var search = {};
        search["email"] = email;
        search['password_id'] = id;

        $.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/api/passwords/share",
                data: JSON.stringify(search),
                dataType: 'json',
                cache: false,
                timeout: 600000,
                success: function (data) {

//                    var json = JSON.stringify(data, null, 4);
        //            var json = JSON.parse(data)
//                    $('#pass_show_'+id).html(data.enc_pass);
//                    $('#pass_checkbox_'+id).prop('checked',true);
//                    $('#pass_button_'+id).html("Schowaj hasło");

                    console.log("SUCCESS : ", data);
                    if(data.status == "shared") alert("Udostępniono");
                    else alert("Błąd udostępniania: "+data.status);
                },
                error: function (e) {
//                    var json = e.responseText;
//                    $('#pass_show_'+id).html(json);

                    console.log("ERROR : ", e);
                    alert("Błąd połączenia");
                }
            });
    }
}