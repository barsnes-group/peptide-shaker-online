

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <meta charset="utf-8">
    <link rel="shortcut icon" href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAD5UExURQAAACQ1RH6SkoKVli2h2tI/LBIXL2xrYoibnJZdqC2d1ZNboxMPJdFGKhK1eIdUt+iJMC2g2fMvItFALC0+TS+59x6wdCw7SiErPS2g25VdpuOGOh5aSYGUlZNbqueHOM89LCs9TOSHOYCTlRYYMR2vbyY8S5ZdqO+JMxyuc8w/K4CTlR6wdOaJO9VALSw9TIGUluSHOiye1phfqn6Tliuf1uKHNZpgqx2odB+ydh6vdCUuQWhLkxcaMh+zd4OXmeSHOy2j3OqKPCw+TBYZMZlfq5deqR6vdNRBLS5ATtVBLS4/Ti6n4tpCLi0/TS9BUJ1ir5xhrdNBLSJTHIoAAAA9dFJOUwBhG+vl4Y8FNOkhHGwkczFj0ybY/SuMnDYyzJ4YyjWSL+A1y+OClc4PPz7a5N+7oszox66svGDFLNj0mDZdQChMAAABH0lEQVQ4y4XRWVuCQBSA4QmBgZS0rIQitsDc27cLULFFTdv+/49piJmQQU/c8XzvGR7mIJR/BAWBj3BRAoVwMhx2MdxLypb+T393dbDvTz6a6/sp7bOmM9CAedKlZV3bPN8wHel7zouV/vC4sydNeXGTzfeiqCA8W+nSjvFh9MoLrx/bStLfGsktFgS+i0exjd1Z2gvCurdqIyJ095xtISeq6pda/RVmtiUmpnUHlc+eX1Jx/AewScVS6qB1Qvb1VAw6yWv5iBNyGPpmkAg6wZ0hh09hxTCCqNdm31w5o5X2A1E0gnZ2lUzElxrt48VtbhdU9D3Wx7vbqCis1saeiE/VAjoR13Bn/wd0pPtwJ6JigJ1s6WoBdoQMEe5E8P0HpPxDW5UCLSMAAAAASUVORK5CYII=">

    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=1">
    <title>LiteMol Frame</title>    
    <link rel="stylesheet" href="VAADIN/litemol/css/LiteMol-plugin.css?lmversion=1518789385303" type="text/css">
    <link rel="stylesheet" href="VAADIN/litemol/css/style.css" />
    <script src="VAADIN/litemol/js/LiteMol-plugin.js"></script>
    


</head>
<body>
    
    <style>
        body {
            font-family: sans-serif;
        }

        #app {
            background: blue;
        }

        .app-default {
            position: absolute;
            margin-top: 100px;
            margin-bottom: 360px;
            margin-left: 10px;
            width: 640px;
            height: 480px;
            left: 270px;
            top: 10px;
        }

        .app-landscape {
            position: fixed;            
            left: 270px;
            right: 40px;
            top: 40px;
            bottom: 40px;
        }

        .app-portrait {
            position: fixed;            
            width: 50%;
            right: 0;
            top: 0;
            bottom: 0;
        }

        #actions {
            position: absolute;
            width: 250px;
            top: 10px;            
            left: 10px;
            bottom: 10px;
            overflow-y: auto;
            padding-right: 10px;         
        }

        #actions > button {
            margin-bottom: 5px;
            display: block;
            width: 100%;
        }

        #actions > input[type=text] {
            width: 100%;
            margin-bottom: 2px;
        }

        #actions > .hover-area {
            padding: 6px 4px;
            background: #66aa99;
            margin-bottom: 5px;
            text-align: center;
            font-size: smaller;
            cursor: default;
        }

        #actions > h4 {
            margin-top: 0;
            margin-bottom: 7px;
        }

        #interactions {
            position: absolute;
            width: 240px;
            top: 10px;
            left: 940px;
            height: 300px;
            overflow-x: auto;
            background: #333;
            color: white;
            line-height: 26px;  
            padding: 0 6px;
        }

        button, input {
            font-size: 14px;    
        }
    </style>
    <div id='actions'>        
    </div>

    <div id='interactions'>
    </div>
<center id="container">
    <div id="app" class="app-default">
    </div>
</center> 
<div>
    <!-- -->
    <div id='progress' style='
         left: 5% !important;
         top: 5% !important;
         /* margin-top: -40px; */
         /* margin-left: -40px; */
         position: absolute;
         z-index: 900000000 !important;
         width: 50px !important;
         height: 50px !important;
         background-image: url("VAADIN/themes/webpeptideshakertheme/img/loading.gif");
         background-repeat:  no-repeat;
         background-size:  contain;
         border-radius:  100%;    
         display:  block;
         visibility:  visible;
         '></div>

    <button title="Show/Hide water, balls & sticks" class="lm-btn lm-btn-link lm-btn-link-toggle-off" style="
            right: 110px !important;
            top:14px !important;
            background:rgba(0,0,0,0) !important;                                                                     
            position: absolute;
            z-index: 900000000;
            padding-top:0px !important;
            font-size: 18px;                                                                      

            " onclick="hideWater()"><span class="lm-icon">&#127778;</span></button>

</div>

<script src="VAADIN/litemol/js/LiteMol-example.js?lmversion=1518789385303"></script>



<script>

    var str = '{"pdbId":"3iuc","chainId":"A"}';
    var controlBtns;
    
            
    var onloadfunction =         function (e) {
        try {
            controlBtns = document.getElementsByTagName('button');
             for (var i = 0; i < controlBtns.length; i++) {

                controlBtns[i].title = "i - " + i;
            }     
    //       controlBtns[0].click();
     //       controlBtns[4].click();
    //       controlBtns[5].click();
     //       setTimeout(finalizeStyle, 2000);
           document.getElementById('showWB').value = !hideWaterVar;
        } catch (exp) {
            alert(exp)
        }

    };
    window.onload = onloadfunction;
    function finalizeStyle() {
        var y = document.getElementsByClassName("lm-btn lm-btn-link");
        y[5].setAttribute("style", " display:none !important; visibility:hidden;");
        y[4].setAttribute("style", " display:none !important; visibility:hidden;");
        y[3].setAttribute("style", " display:none !important; visibility:hidden;");

    }


    function getParameterByName(name, url) {
        if (!url)
            url = window.location.href;
        name = name.replace(/[\[\]]/g, "\\$&");
        var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
                results = regex.exec(url);
        if (!results)
            return null;
        if (!results[2])
            return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    }

    //     var tquery = '{"pdbId":"6asy","chainId":"A","coloring":{"entries":[{"start_residue_number":0,"color":{"r":124,"b":124,"g":124},"end_residue_number":1000,"struct_asym_id":"A","entity_id":"1"},{"start_residue_number":618,"color":{"r":0,"b":0,"g":255},"end_residue_number":629,"struct_asym_id":"A","entity_id":"1"},{"start_residue_number":10,"color":{"r":0,"b":255,"g":0},"end_residue_number":50,"struct_asym_id":"A","entity_id":"1"},{"start_residue_number":120,"color":{"r":255,"b":0,"g":0},"end_residue_number":300,"struct_asym_id":"A","entity_id":"1"}],"base":{"r":255,"b":255,"g":255}}}';
    var tquery = '{"pdbId":"3iuc","chainId":"A","coloring":{"entries":[{"start_residue_number":44,"color":{"r":255,"b":0,"g":0},"end_residue_number":404,"struct_asym_id":"A","entity_id":"1"}],"base":{"r":255,"b":255,"g":255}}}';

    function excutequery(query, newId) {
        document.getElementById("progress").style.display = "block";
        tquery = query;
        document.getElementById('pdbid').value = tquery;
        if (newId) {
            reset();
            controlBtns[14].click();
            setTimeout(update, 5000);
        } else {
            update();
        }

    }
    function update() {
        controlBtns[24].click();
        controlBtns[19].click();
        document.getElementById("progress").style.display = "none";

    }
    function reset() {
        document.getElementById("progress").style.display = "none";
        controlBtns[26].click();
    }


    var colorCode2 = {r: 200, g: 0, b: 000};
    var colorCode = {r: 255, g: 255, b: 255};
    var selectioncolorCode = {r: 0, g: 0, b: 200};
    var selection = function (entity, chain, start, end) {
        var selectionDetails = {
            entity_id: entity,
            struct_asym_id: chain,
            start_residue_number: start,
            end_residue_number: end
        };
        return selectionDetails;

    };


    var selectionDetails = {
        entity_id: '1',
        struct_asym_id: 'A',
        start_residue_number: 28,
        end_residue_number: 406
    };

    var selectionDetails2 = {
        entity_id: '1',
        struct_asym_id: 'B',
        start_residue_number: 1,
        end_residue_number: 100
    };
    var visualParams = {
        polymer: true,
        polymerRef: 'polymer-visual',
        het: false,
        hetRef: 'het-visual',
        water: false,
        waterRef: 'water-visual'
    };
    var hideWaterVar = true;
    function hideWater() {
        document.getElementById('showWB').value = hideWaterVar;
        hideWaterVar = !hideWaterVar;
        excutequery(tquery, true);
    }
    hideWater();

</script>    

</body>
</html>
