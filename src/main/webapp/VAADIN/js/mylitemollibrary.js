
// Define the namespace
var mylitemollibrary = mylitemollibrary || {};
mylitemollibrary.LiteMolComponent = function (element) {
    var document = element.ownerDocument;
    var imported = document.createElement('script');
    imported.src = 'VAADIN/litemol/js/LiteMol-example.js?lmversion=1518789385303';
    document.head.appendChild(imported);
    var hideWaterBtn;
    var controlBtns;
    var updatingBtn;
    var hideWaterVar = true;
    var tquery = '{"pdbId":"3iuc","chainId":"A","coloring":{"entries":[{"start_residue_number":44,"color":{"r":255,"b":0,"g":0},"end_residue_number":404,"struct_asym_id":"A","entity_id":"1"}],"base":{"r":255,"b":255,"g":255}}}';
    var init = false;
    var latestValue = 'ToTestNotRealValue';
    element.innerHTML =
            " <div id='actions'> </div>" +
            " <div id='interactions'></div>" +
            "<center id='container' style='position: relative;'><div id='app' class='app-default'> </div></center> " +
            ""
            + " <style>body {ont-family: sans-serif;}"
            + "  #app {  background: transparent; }"
            + " .app-default { position: relative; width: 95%; height: 95%;}"
            + "  .app-landscape {position: fixed;  left: 270px;   right: 40px;            top: 40px;            bottom: 40px;        }"
            + ".app-portrait {            position: fixed;                        width: 50%;            right: 0;            top: 0;            bottom: 0;        }"
            + "        #actions {       display:none;     position: absolute;            width: 250px;            top: 10px;                        left: 10px;            bottom: 10px;            overflow-y: auto;            padding-right: 10px;                 }"
            + " #actions > button {            margin-bottom: 5px;            display: block;            width: 100%;        }\n\
            #actions > input[type=text] {    width: 100%;            margin-bottom: 2px;        }" +
            " #actions > .hover-area {            padding: 6px 4px;            background: #66aa99;            margin-bottom: 5px;            text-align: center;            font-size: smaller;            cursor: default;        }"
            + "  #actions > h4 {            margin-top: 0;            margin-bottom: 7px;        }"
            + "        #interactions {      display:none;      position: absolute;            width: 240px;            top: 10px;            left: 940px;            height: 300px;            overflow-x: auto;            background: #333;            color: white;            line-height: 26px;              padding: 0 6px;        }" +
            +"  button, input {            font-size: 14px;} \n\
           </style>";
    
    
    // Getter and setter for the value property
    this.getValue = function () {
        
    };
    
    this.setValue = function (value) {
        try {
            
            if ((value === null) || (value === undefined) || latestValue.includes(value)) {
                return;
            }
            latestValue = value + "";
//            if (value.includes("redraw-_-")) {
//                 setTimeout(hideWater, 1000);
//                alert(value);
//            } else 
                if (value.includes("reset-_-")) {
                reset();
            } else if (value.includes("query-_-")) {
                if (tquery.includes(value.split("-_-")[1])) {
                    return;
                }
                tquery = value.split("-_-")[1];
                var newid = (value.split("-_-")[2] === 'true');
                if (!init) {
                    init = true;
                    initplugin();
                }
                excutequery(newid);
            } else if (value.includes("update-_-")) {
                latestValue = "";
                redraw();
            }
        } catch (exp) {
            alert(exp);
        }
        
    };
    window.alert = function () {};
// Default implementation of the click handler
    this.click = function () {
        alert("Error: Must implement click() method");
    };
    
    var initplugin = function () {
        try {
            controlBtns = document.getElementsByTagName('button');
            controlBtns[0].click();
            controlBtns[5].click();
            finalizeStyle();
            controlBtns[19].click();
            controlBtns[24].click();
            controlBtns[26].click();
        } catch (exp) {
            alert("init blug error?? " + exp);
        }
        
    };
    
    $(function () {
        try {
            controlBtns = document.getElementsByTagName('button');
            
        } catch (exp) {
//            alert(exp);
        }
    });
    
    function finalizeStyle() {
        try {
            var y = document.getElementsByClassName("lm-btn lm-btn-link");
            updatingBtn = y[4];
            updatingBtn.setAttribute("style", " display:none !important; visibility:hidden;");
            hideWaterBtn = y[3];
            hideWaterBtn.removeChild(hideWaterBtn.firstChild);
            hideWaterBtn.innerHTML = "<span class='lm-icon'>&#127778;</span>";
            hideWaterBtn.onclick = hideWater;
            hideWaterBtn.title = 'Show/Hide water, balls & sticks';
            var app = document.getElementById("app");
            app.setAttribute("style", " z-index:1 !important; background-color:transparent !important;");
            app.style.zIndex = 1;
        } catch (exp) {
            alert("finalize style error " + exp)
        }
        
    }
    
    var redraw = function () {
        hideWaterVar = !hideWaterVar;
        setTimeout(hideWater, 1000);
    };
    
    function excutequery(newId) {
        try {
            document.getElementById('pdbid').value = tquery;
            if (newId === true) {
                reset();
                finalizeStyle();
                controlBtns[14].click();
                document.getElementById('showWB').value = true;
                setTimeout(update, 4000);
            } else {
                
                update();
            }
            
        } catch (exception) {
            alert("exc query excp " + exception)
            
        }
        
        
    }
    
    function update() {
        controlBtns[24].click();
        controlBtns[19].click();
        
    }
    
    function reset() {
        controlBtns[26].click();
    }
    function hideWater() {
        controlBtns[26].click();
        document.getElementById('showWB').value = hideWaterVar;
        hideWaterVar = !hideWaterVar;
        setTimeout(excutequery(true), 1000);
    }
    
    
    
    
    
    
};