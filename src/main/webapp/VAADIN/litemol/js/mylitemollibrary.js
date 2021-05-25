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
    var init = false;
    var delay = false;
    var latestValue = 'ToTestNotRealValue';
    var jsonFullQuery = JSON.parse('{"pdbId":"3iuc"}');
    var parentElement = null;
    element.innerHTML = " <div id='actions'> </div>" +
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
    this.getValue = function () {};
    this.setValue = function (value) {
        try {
            if ((typeof value === 'undefined')) {
                return;
            }

            jsonFullQuery = JSON.parse(value);
            document.getElementById('pdbid').value = JSON.stringify(jsonFullQuery.values, null, 2);
            locateParentElement();
            if (parentElement === null || (jsonFullQuery === null) || (jsonFullQuery === undefined) || latestValue.localeCompare(value) === 0) {
                return;
            }
            latestValue = value;
            if (jsonFullQuery.type.localeCompare("reset") === 0) {
                reset();
            } else if (jsonFullQuery.type.localeCompare("query") === 0) {
                if (!init && isParentVisible()) {
                    init = true;
                    initplugin();
                    delay = true;
                }
                if (init) {
                    if (delay) {
                        delay = false;
                        setTimeout(excutequery(jsonFullQuery.newid), 5000);
                    } else {
                        excutequery(jsonFullQuery.newid);
                    }
                }
            } else if (jsonFullQuery.type.localeCompare("update") === 0) {
                latestValue = "";
                window.alert("update - redraw");
                redraw();

            }
        } catch (exp) {
            alert("error at 70: " + exp);
        }

    };
    function  isParentVisible() {
        return true;
        if (parentElement === null) {
            return false;
        } else if (parentElement.classList.contains('v-absolutelayout-wrapper-hidepanel')) {
            return false;
        } else {
            return true;
        }
    }
    function isDescendant(parent, child) {
        var node = child.parentNode;
        while (node !== null) {
            if (node === parent) {
                return true;
            }
            node = node.parentNode;
        }
        return false;
    };
    function locateParentElement() {
        var containers = document.getElementsByClassName("v-absolutelayout-wrapper v-absolutelayout-wrapper-transitionallayout");
        for (var i = 0; i < containers.length; i++) {
            var elem = containers[i];
            if (isDescendant(elem, document.getElementById('app'))) {
                parentElement = elem;
            }
        }
    };
    this.click = function () {
        alert("Error: Must implement click() method");
    };
    var initplugin = function () {
        try {
            if (document.getElementsByTagName('h6').length < 27) {
                init = false;
                return;
            }
            document.getElementById('pdbid').value =  JSON.stringify(jsonFullQuery.values, null, 2);
            controlBtns[0].click();
            controlBtns[5].click();
            finalizeStyle();
            controlBtns[19].click();
            controlBtns[24].click();
            controlBtns[26].click();

        } catch (exp) {
            alert("init blug error " + exp);
        }

    };
    $(function () {
        try {
            jsonFullQuery = JSON.parse('{"pdbId":"3iuc"}');
            controlBtns = document.getElementsByTagName('h6');
            initplugin();
        } catch (exp) {
            alert("error at loading function " + exp);
        }
    });
    function finalizeStyle() {
        try {
            var y = document.getElementsByClassName("lm-btn lm-btn-link");
            updatingBtn = y[4];
            if (updatingBtn !== null && (typeof updatingBtn !== 'undefined')) {
                updatingBtn.setAttribute("style", " display:none !important; visibility:hidden;");
            }

            hideWaterBtn = y[3];
            if (hideWaterBtn !== null && (typeof hideWaterBtn !== 'undefined')) {
                hideWaterBtn.removeChild(hideWaterBtn.firstChild);
                hideWaterBtn.innerHTML = "<span class='lm-icon'>&#127778;</span>";
                hideWaterBtn.onclick = hideWater;
                hideWaterBtn.title = 'Show/Hide water, balls & sticks';
            }
            var app = document.getElementById("app");
            app.setAttribute("style", " z-index:1 !important; background-color:transparent !important;");
            app.style.zIndex = 1;
        } catch (exp) {
            alert("finalize style error " + exp);
        }

    };
    var redraw = function () {
        hideWaterVar = !hideWaterVar;
        setTimeout(hideWater, 1000);
    };
    function excutequery() {
        try {
            document.getElementById('pdbid').value = JSON.stringify(jsonFullQuery.values, null, 2);
            if (jsonFullQuery.newid === true) {
                reset();
                finalizeStyle();
                controlBtns[14].click();
                document.getElementById('showWB').value = false;
                setTimeout(update, 4000);
            } else {
                update();
            }

        } catch (exception) {
            alert("exc query excp " + exception);
        }


    };
    function update() {
        controlBtns[24].click();
        controlBtns[19].click();
    };
    function reset() {
        controlBtns[26].click();
    };
    function hideWater() {
        controlBtns[26].click();
        document.getElementById('showWB').value = hideWaterVar;
        hideWaterVar = !hideWaterVar;
        setTimeout(excutequery(true), 1000);
    };
//    window.alert = function () {
//    };


};
