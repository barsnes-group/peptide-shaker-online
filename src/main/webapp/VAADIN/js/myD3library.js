// Define the namespace
var myD3library = myD3library || {};
myD3library.VennDiagramComponent = function (element) {
    element.innerHTML = "<div id='chartcontainer' style='width:100% !important;height:100% !important; cursor:pointer;'><div id='venn'  style='width:100% !important;height:100% !important'></div></div>";

    var self = this;
    var doneLoading = false;
    var stopevent = true;
    var selectionAction = false;
    var chart = venn.VennDiagram();
    var selectedDataColours = [];

    var unselectedDataColors = [];
    var sets = null;
    var selectedData = [];
    var cwidth;
    var cheight;

    function initChart(width, height, data, selectedDataColors, unselectedDataColors) {


        doneLoading = true;
        chart.width(width)
            .height(height);
        var div = d3.select("#venn");
        try {
            clearchart();
            d3.select("#venn").datum(data).call(chart);
        } catch (err) {
            if (!err.toString().includes("select")) {
                doneLoading = false;
                self.click();
            }
        }
        //fill color
        div.selectAll("path")
            .style("fill-opacity", 1)
            .style("stroke", function (d, i) {
                return "white";
            })

            //                .style("fill-opacity", function (d, j) {
            //            return  0.8;
            //        })
            .style("fill", function (d, j) {
                return unselectedDataColors[j];
            }).style("stroke-width", 3);
//                        


        var areas = d3.selectAll("#venn g");
        areas.select("path")
//                .filter(function (d) {
//                    return d.sets.length === 1;
//                })
//                .style("fill-opacity", 1)
//                .style("stroke-width", 5)
//                .style("stroke-opacity", .8)
//                .style("fill", function (d, i) {
//                    return colours[i];
//                })
        ;
//                .style("stroke", function (d, i) {
//                    return colours[i];
//                });

        areas.select("text")
            .style("fill", "#ffffff")
            .style("font-family", "'Open Sans', sans-serif")
            .style("font-size", "16px").style("font-weight", 100).style("font-style", "normal").style("text-decoration", "none").style("stroke-width", "0.1px").style("stroke", "white");

        /*    fill: black;
         font-size: 25px;
         stroke: orange;
         font-weight: 500;
         stroke-width: 0.3px;
         font-family: 'open sans';
         font-style: normal;
         text-decoration: underline;*/


        /*
         *     fill: white;
         font-size: 25px;
         font-weight: 100;
         stroke-width: 0.1px;
         font-family: 'open sans';
         font-style: italic;
         **/


//        var defs = d3.select("#venn svg").append("defs");

        // from http://stackoverflow.com/questions/12277776/how-to-add-drop-shadow-to-d3-js-pie-or-donut-chart
//        var filter = defs.append("filter")
//                .attr("id", "dropshadowfilter")
//
//        filter.append("feGaussianBlur")
//                .attr("in", "SourceAlpha")
//                .attr("stdDeviation", 1)
//                .attr("result", "blur");
//        filter.append("feOffset")
//                .attr("in", "blur")
//                .attr("dx", 1)
//                .attr("dy", 1)
//                .attr("result", "offsetBlur");
//
//        var feMerge = filter.append("feMerge");
//
//        feMerge.append("feMergeNode")
//                .attr("in", "offsetBlur")
//        feMerge.append("feMergeNode")
//                .attr("in", "SourceGraphic");
//
//        areas.attr("filter", "url(#dropshadowfilter)");
//


        div.selectAll("g").attr("pointer-events", "all");

        //unselect all
        div.selectAll("#venn .venn-area").on("click", function (d, i) {
            stopevent = true;
            selectedData[i] = !selectedData[i];
            selectCircle();
        });
        //select circle
        d3.select("#venn").on("click", function (g, i) {
            if (stopevent) {
                stopevent = false;
                return;
            }
            for (i = 0; i < selectedData.length; i++) {
                selectedData[i] = false;
            }
            selectCircle();
        });
        div.selectAll("g")
            .on("mouseover", function (d, i) {
            })

            .on("mousemove", function () {

            })

            .on("mouseout", function (d, i) {

            });


    }

    function highlightCircle() {
        selectionAction = true;
        d3.select("#venn").selectAll("path").style("fill", function (d, j) {
            if (selectedData[j]) {
                return selectedDataColours[j];
            } else {
                return unselectedDataColors[j];
            }
        });
    }

    function selectCircle() {
        selectionAction = true;
        var countselect = 0;
        d3.select("#venn").selectAll("path").style("fill", function (d, j) {
            if (selectedData[j]) {
                countselect++;
                return selectedDataColours[j];
            } else {
                return unselectedDataColors[j];
            }
        });
        if (countselect === selectedDataColours.length) {
            for (var i = 0; i < selectedData.length; i++) {
                selectedData[i] = false;
            }
            highlightCircle();
        }
//        d3.selectAll("#venn g").select("text")
//                .style("fill", function (d, j) {
//                    if (selectedData[j]) {
//                        return "black";
//                    } else {
//                        return "white";
//                    }
//                }).style("font-weight", function (d, j) {
//            if (selectedData[j]) {
//                return 500;
//            } else {
//                return 100;
//            }
//        }).style("font-style", function (d, j) {
//            if (selectedData[j]) {
//                return "normal";
//            } else {
//                return "italic";
//            }
//        }).style("text-decoration", function (d, j) {
//            if (selectedData[j]) {
//                return "underline";
//            } else {
//                return "none";
//            }
//        }).style("stroke-width", function (d, j) {
//            if (selectedData[j]) {
//                return "0.3px";
//            } else {
//                return "0.1px";
//            }
//        }).style("stroke", function (d, j) {
//            if (selectedData[j]) {
//                return "orange";
//            } else {
//                return "white";
//            }
//        });
        self.click();
    }

    function selectAllCircles() {
//        d3.selectAll("#venn g").select("text")
//                .style("fill", "#ffffff")
//                .style("font-family", "'Open Sans', sans-serif")
//                .style("font-size", "16px").style("font-weight", 100).style("font-style", "normal").style("text-decoration", "none").style("stroke-width","0.1px").style("stroke","white");
        d3.select("#venn").selectAll("path").style("fill", function (d, j) {
            selectedData[j] = false;
            return unselectedDataColors[j];

        });
        self.click();
    }

    function clearchart() {
        var myNode = document.getElementById("venn");
        if (myNode === null)
            return;
        while (myNode.firstChild) {
            myNode.removeChild(myNode.firstChild);
        }

    }

    this.setValue = function (value) {
        if (value.startsWith("serverRequest:")) {
            if (value.includes(":sizeonly") && sets !== null) {
                var arr = value.split(";");
                if (cwidth === parseInt(arr[1]) && cheight === parseInt(arr[2])) {
                    return;
                }
                cwidth = parseInt(arr[1]);
                cheight = parseInt(arr[2]);
                initChart(parseInt(arr[1]), parseInt(arr[2]), sets, selectedDataColours, unselectedDataColors);
                highlightCircle();
            }
            if (value.includes("selection") && sets !== null) {

                var selectionIndexs = JSON.parse(value.split(";")[1].split("selection:")[1]);
                for (var i = 0; i < selectedDataColours.length; i++) {
                    selectedData[i] = false;
                }
                for (var i = 0; i < selectionIndexs.length; i++) {
                    selectedData[selectionIndexs[i]] = true;
                }

                highlightCircle();
            }
            if (value.includes(":data")) {
                sets = JSON.parse(value.split(";")[1]);
                selectedDataColours = JSON.parse(value.split(";")[2]);
                unselectedDataColors = JSON.parse(value.split(";")[3]);
                selectedData = [];
                for (var i = 0; i < selectedDataColours.length; i++) {
                    selectedData[i] = false;
                }
                cwidth = parseInt(value.split(";")[4].split(",")[0]);
                cheight = parseInt(value.split(";")[4].split(",")[1]);
                initChart(cwidth, cheight, sets, selectedDataColours, unselectedDataColors);
                selectAllCircles();

            }


        }
    };

    this.click = function () {
        alert("Error: Must implement click() method");
    };

    this.getValue = function () {
        if (doneLoading === false) {
            return "error_in_loading";
        } else if ((doneLoading === true) && (selectionAction === false)) {
            return "loading_is_done";
        }
        var valueText = "";
        for (var t = 0; t < selectedData.length; t++) {
            if (selectedData[t]) {
                valueText += t + ",";
            }
        }
        ;
        selectionAction = false;
        return valueText;
    };
};