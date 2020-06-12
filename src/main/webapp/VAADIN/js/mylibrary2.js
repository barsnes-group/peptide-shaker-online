
// Define the namespace
var mylibrary2 = mylibrary2 || {};
mylibrary2.SelectioncanvasComponent2 = function (element) {
    element.innerHTML =
            "<canvas id='selectioncanvas2' oncontextmenu='return false;'></canvas>" +
            "<input id='coords2' style='visibility:hidden;' type='text' name='value'/>" +
            "<input id='tbox2' style='visibility:hidden;' type='text' name='value'/>" +
            "<input id='hiddenbtn2' style='visibility:hidden;' type='button' value='Click'/>";
    var document = element.ownerDocument;
    var thecoordsText = document.getElementById('coords2');
    var tBox = document.getElementById('tbox2');
    tBox.style.visibility = "hidden";
    thecoordsText.style.visibility = "hidden";
    var cBtn = document.getElementById('hiddenbtn2');
    cBtn.style.visibility = "hidden";

    window.alert = function () {};

    window.onload = function () {
        var frameWidth = document.getElementById()("selectioncanvas2").offsetWidth;
        var frameHeight = document.getElementById("selectioncanvas2").offsetHeight;
        alert(frameWidth + "  " + frameHeight);

    };
    window.onresize = function (event) {
        var frameWidth = document.getElementById()("selectioncanvas2").offsetWidth;
        var frameHeight = document.getElementById("selectioncanvas2").offsetHeight;
        theCanvas.width = frameWidth;
        theCanvas.height = frameHeight;
    };


    // Style it
    // Getter and setter for the value property
    this.getValue = function () {
        return element.getElementsByTagName("input")[0].value;
    };
    var freeYAccess;
    this.setValue = function (value) {
        var res = value.split(",");
        if (res.length === 3) {
            theCanvas.width = parseInt(res[0], 10);
            theCanvas.height = parseInt(res[1], 10);
            freeYAccess = ('true' === res[2]);
        }
    };
    // Default implementation of the click handler
    this.click = function () {
        alert("Error: Must implement click() method");
    };
    // Set up button click
    var button = element.getElementsByTagName("input")[1];
    var self = this; // Can't use this inside the function




    button.onclick = function () {
        self.click();
        button.disabled = true;
        setTimeout(activateBtn, 1000);
    };
    function activateBtn() {
        button.disabled = false;
    }







    var theCanvas = document.getElementById('selectioncanvas2');
    var ctx = theCanvas.getContext('2d');
    var drawLine = false;
    var mouseevent = false;
    var touchevent = false;
    var finalPos = {x: -1, y: -1};
    var startPos = {x: 0, y: 0};
    var finalTouchI = {x: -1, y: -1};
    var startTouchI = {x: -1, y: -1};
    var finalTouchII = {x: -1, y: -1};
    var startTouchII = {x: -1, y: -1}
    var btntype = "-500";
    var cs = getComputedStyle(theCanvas);
/// these will return dimensions in *pixel* regardless of what
/// you originally specified for image:
    var width = parseInt(cs.getPropertyValue('width'), 10);
    var height = parseInt(cs.getPropertyValue('height'), 10);
/// now use this as width and height for your canvas element:
    theCanvas.width = width;
    theCanvas.height = height;
    thecoordsText.value = width + "," + height;
    var canvasOffset = $('#selectioncanvas2').offset();
    var selectionCanvasElement = $('#selectioncanvas2');

    function line(cnvs) {
        clearCanvas();
        cnvs.lineWidth = 0.3;
        cnvs.beginPath();
        cnvs.fillStyle = "rgba(184, 207, 224,0.1)";
        if (freeYAccess) {
            cnvs.rect(startPos.x, startPos.y, (finalPos.x - startPos.x), (finalPos.y - startPos.y));
            ctx.fillRect(startPos.x, startPos.y, (finalPos.x - startPos.x), (finalPos.y - startPos.y));

        } else {
            cnvs.rect(startPos.x, 5, (finalPos.x - startPos.x), (theCanvas.height - 10));
            ctx.fillRect(startPos.x, 5, (finalPos.x - startPos.x), (theCanvas.height - 10));
        }

        cnvs.stroke();
    }

    function rect(cnvs) {
        clearCanvas();
        cnvs.lineWidth = 0.3;
        cnvs.fillStyle = "rgba(184, 207, 224,0.1)";
        cnvs.beginPath();
        var startX = Math.min(finalTouchI.x, finalTouchII.x);
        var finalX = Math.max(finalTouchI.x, finalTouchII.x);

        var startY = Math.min(finalTouchI.y, finalTouchII.y);
        var finalY = Math.max(finalTouchI.y, finalTouchII.y);
        if (freeYAccess) {
            cnvs.rect(startX, startY, (finalX - startX), (finalY - startY));
            ctx.fillRect(startX, startY, (finalX - startX), (finalY - startY));

        } else {
            cnvs.rect(startX, 5, (finalX - startX), (theCanvas.height - 10));
            ctx.fillRect(startX, 5, (finalX - startX), (theCanvas.height - 10));
        }
        cnvs.stroke();
    }

    function clearCanvas() {
        ctx.clearRect(0, 0, theCanvas.width, theCanvas.height);
    }

    $('#selectioncanvas2').mousemove(function (e) {
        if (touchevent)
            return;
        if (drawLine === true && mouseevent) {
            if (freeYAccess) {
                finalPos = {x: e.pageX - canvasOffset.left, y: e.pageY - canvasOffset.top};
            } else {
                finalPos = {x: e.pageX - canvasOffset.left, y: startPos.y};
            }
            clearCanvas();
            line(ctx);
        }
    });

    $('#selectioncanvas2').mousedown(function (e) {
        if (touchevent)
            return;
        btntype = e.which;
        mouseevent = true;
        drawLine = true;
        ctx.strokeStyle = 'black';
        ctx.lineWidth = 0.3;
        ctx.lineCap = 'round';
        ctx.beginPath();
        startPos = {x: e.pageX - canvasOffset.left, y: e.pageY - canvasOffset.top};

    });

    $('#selectioncanvas2').mouseup(function () {
        if (touchevent)
            return;
        mouseevent = false;
        clearCanvas();
        // Replace with var that is second canvas
        thecoordsText.value = startPos.x + "," + startPos.y + "," + finalPos.x + "," + finalPos.y + "," + btntype;
        line(ctx);
        finalPos = {x: -1, y: -1};
        startPos = {x: -1, y: -1};
        drawLine = false;
        ctx.beginPath();
        clearCanvas();
        document.dis;
        button.click(btntype);
        btntype = "-500";
    });




    selectionCanvasElement.touch({preventDefault: {
            drag: true,
            swipe: true,
            tap: true,
            panzoom: true,
            contextmenu: true
        }});

    selectionCanvasElement.on('doubleTap', function (e) {
        touchevent = true;
        zoomout();
        touchevent = false;
    });

    selectionCanvasElement.on('touchstart', function (ev) {
        if (mouseevent)
            return;
        touchevent = true;
        if (ev.originalEvent.touches.length !== 2)
            return;
        ctx.strokeStyle = '#626262';
        ctx.lineWidth = 0.3;
        ctx.lineCap = 'round';
        ctx.beginPath();
        clearCanvas();
        startTouchI = {x: parseInt(ev.originalEvent.touches[0].pageX - canvasOffset.left, 10), y: parseInt(ev.originalEvent.touches[0].pageY - canvasOffset.top, 10)};
        startTouchII = {x: parseInt(ev.originalEvent.touches[1].pageX - canvasOffset.left, 10), y: parseInt(ev.originalEvent.touches[1].pageY - canvasOffset.top, 10)};
    });

    selectionCanvasElement.on('touchend', function (ev) {
        if (mouseevent) {
            return;
        }
        setTimeout(touchEnd, 500);
        ctx.beginPath();
        clearCanvas();
        document.dis;
    });
    function zoomout() {
        startPos.x = -1;
        startPos.y = -1;
        finalPos.x = -1;
        finalPos.y = -1;
        thecoordsText.value = startPos.x + "," + startPos.y + "," + finalPos.x + "," + finalPos.y + "," + 2;
        button.click(2);
    }
    function  touchEnd() {

        //get direction of each touch
        var firstTouchToLeft = false;
        var secoundTouchToLeft = false;
        var firstTouchDistance = -1;
        var secoundTouchDistance = -1;
        if (startTouchI.x > finalTouchI.x) {
            firstTouchToLeft = true;
        }
        if (startTouchII.x > finalTouchII.x) {
            secoundTouchToLeft = true;
        }
        firstTouchDistance = (startTouchI.x - finalTouchI.x) / width;
        secoundTouchDistance = (startTouchII.x - finalTouchII.x) / width;
        if (firstTouchToLeft === secoundTouchToLeft) {
            if (firstTouchToLeft) {
                //zoom in mainly from left to right
                var min = Math.min(startTouchI.x, startTouchII.x);
                var max = Math.max(finalTouchI.x, finalTouchII.x);

            }
        } else if (firstTouchToLeft !== secoundTouchToLeft) {

            if ((firstTouchToLeft && firstTouchDistance > 0 && secoundTouchDistance <= 0)) {
                if (startTouchI.x <= startTouchII.x) {

                    //to test 
                    //
                    startPos.x = finalTouchI.x;
                    startPos.y = 100;
                    finalPos.x = finalTouchII.x;
                    finalPos.y = 100;

                }
            } else if ((secoundTouchToLeft && secoundTouchDistance > 0 && firstTouchDistance <= 0)) {
                if (startTouchI.x >= startTouchII.x) {
                    startPos.x = finalTouchII.x;
                    startPos.y = 100;
                    finalPos.x = finalTouchI.x;
                    finalPos.y = 100;
                }
            }
            thecoordsText.value = startPos.x + "," + startPos.y + "," + finalPos.x + "," + finalPos.y + "," + 1;
            button.click("1");
        }
        touchevent = false;
    }
    var timer;
    selectionCanvasElement.on('touchmove', function (ev) {
        if (mouseevent)
            return;
        touchevent = true;
        if (ev.originalEvent.touches.length !== 2)
            return;
        finalTouchI = {x: parseInt(ev.originalEvent.touches[0].pageX - canvasOffset.left, 10), y: parseInt(ev.originalEvent.touches[0].pageY - canvasOffset.top, 10)};
        finalTouchII = {x: parseInt(ev.originalEvent.touches[1].pageX - canvasOffset.left, 10), y: parseInt(ev.originalEvent.touches[1].pageY - canvasOffset.top, 10)};
        rect(ctx);
        clearTimeout(timer);
        timer = setTimeout(touchEnd, 300);
    });

};