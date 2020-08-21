package com.uib.web.peptideshaker.presenter.core.graph;

import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.itextpdf.text.pdf.codec.Base64;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.ProteinGroupObject;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.RangeColorGenerator;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.Legend;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.DragAndDropWrapper.WrapperTargetDetails;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.themes.ValoTheme;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import graphmatcher.NetworkGraphComponent;
import org.jfree.chart.encoders.ImageEncoder;
import org.jfree.chart.encoders.ImageEncoderFactory;
import org.jfree.chart.encoders.ImageFormat;
import org.vaadin.simplefiledownloader.SimpleFileDownloader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * This class represents Graph layout component
 *
 * @author Yehia Farag
 */
public abstract class GraphComponent extends VerticalLayout {

    private final AbsoluteLayout canvas;
    private final Stroke dashLineStroke;
    private final AbsoluteLayout mainContainer;
    private final AbsoluteLayout proteoformLayerContainer;
    private final DropHandler dropHandler;
    private final OptionGroup graphsControl;
    private final AbsoluteLayout proteinPeptideGraphWrapper;
    private final SizeReporter sizeReporter;
    private final Map<String, String> styles;
    private final Label graphInfo;
    private final HorizontalLayout bottomRightPanel;
    private final PopupView legendLayout;
    private final Legend informationLegend;
    private final HorizontalLayout rightBottomPanel;
    private final VerticalLayout lefTtopPanel;
    private final Map<Object, Node> nodesMap;
    private final Set<Edge> edgesMap;
    private final OptionGroup proteinsControl;
    private final OptionGroup nodeControl;
    private final Set<Object> selectedProteins;
    private final Set<Object> selectedPeptides;
    private final AbsoluteLayout scaleContainer;
    private final Property.ValueChangeListener proteinsControlListener;
    private int liveWidth;
    private int liveHeight;
    private Image edgesImage;
    /**
     * The graph.
     */
    private UndirectedSparseGraph<String, String> graph;
    /**
     * The nodes.
     */
    private Map<String, ProteinGroupObject> proteinNodes;
    private Map<String, PeptideObject> peptidesNodes;
    /**
     * The edges: the keys are the node labels and the elements the list of
     * objects.
     */
    private HashMap<String, ArrayList<String>> edges;
    /**
     * Creates new form GraphForm
     */
    private VisualizationViewer visualizationViewer;
    private FRLayout graphLayout;
    private boolean uniqueOnly = false;
    private Object lastSelected;
    private String thumbImgeUrl;
    private VerticalLayout psmColorScaleLayout;
    private VerticalLayout intensityColorScaleLayout;
    private NetworkGraphComponent proteinsPathwayNewtorkGraph;
    private String lastSelectedModeType = null;

    public GraphComponent() {
        GraphComponent.this.setMargin(new MarginInfo(false, false, false, false));
        this.dashLineStroke = new BasicStroke(0.5f, // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                5.0f, new float[]{5.0f}, 0.0f);

        nodesMap = new HashMap<>();
        edgesMap = new HashSet<>();
        styles = new HashMap<>();
        this.selectedProteins = new HashSet<>();
        this.selectedPeptides = new HashSet<>();

        styles.put("Confident", "greenbackground");
        styles.put("Doubtful", "orangebackground");
        styles.put("Not Validated", "redbackground");
        styles.put("Not Available", "graybackground");
        styles.put("Protein", "greenbackground");
        styles.put("Transcript", "orangebackground");
        styles.put("Homology", "seabluebackground");
        styles.put("Predicted", "purplebackground");
        styles.put("Uncertain", "redbackground");
        styles.put("Not Applicable", "lightgraybackground");

        //init main layout
        //calculate canavas dimension 
        mainContainer = new AbsoluteLayout();
        mainContainer.setWidth(100, Unit.PERCENTAGE);
        mainContainer.setHeight(100, Unit.PERCENTAGE);
        mainContainer.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            if (event.getClickedComponent() instanceof AbsoluteLayout) {
                selectNodes(new Object[]{});
            }
        });

        bottomRightPanel = new HorizontalLayout();
        bottomRightPanel.setSpacing(false);
        bottomRightPanel.setStyleName("inframe");

        rightBottomPanel = new HorizontalLayout();
        rightBottomPanel.setSpacing(true);

        graphInfo = new Label();
        graphInfo.setContentMode(ContentMode.HTML);
        graphInfo.setStyleName(ValoTheme.LABEL_LIGHT);
        graphInfo.addStyleName(ValoTheme.LABEL_SMALL);
        graphInfo.addStyleName(ValoTheme.LABEL_TINY);

        graphInfo.addStyleName("inframe");
        rightBottomPanel.addComponent(graphInfo);

        VerticalLayout updateLayoutBtn = new VerticalLayout();
        updateLayoutBtn.setIcon(VaadinIcons.REFRESH);
        updateLayoutBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        updateLayoutBtn.addStyleName(ValoTheme.BUTTON_LINK);
        updateLayoutBtn.addStyleName("refreshbtn");
        updateLayoutBtn.setDescription("Update layout");
        updateLayoutBtn.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            updateGraphLayout();
        });

        VerticalLayout selectAllBtn = new VerticalLayout();
        selectAllBtn.setIcon(new ThemeResource("img/selectall_1.png"));
        selectAllBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        selectAllBtn.addStyleName(ValoTheme.BUTTON_LINK);
        selectAllBtn.addStyleName("selectallbtn");
        selectAllBtn.setDescription("Select all");
        selectAllBtn.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            selectAll();
        });

        informationLegend = new Legend(false) {
            @Override
            public void close() {
                legendLayout.setPopupVisible(false);
            }

        };
        legendLayout = new PopupView(null, informationLegend) {
            @Override
            public void setPopupVisible(boolean visible) {
                this.setVisible(visible);
                super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
            }

        };

        legendLayout.setHideOnMouseOut(false);
        legendLayout.addStyleName("protlegend");
        legendLayout.addStyleName(ValoTheme.BUTTON_TINY);
        legendLayout.addStyleName(ValoTheme.BUTTON_LINK);
        legendLayout.setVisible(false);

        VerticalLayout legendLayoutBtn = new VerticalLayout();
        legendLayoutBtn.setIcon(new ThemeResource("img/legend.png"));
        legendLayoutBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        legendLayoutBtn.addStyleName(ValoTheme.BUTTON_LINK);
        legendLayoutBtn.addStyleName("legendbtn");
        legendLayoutBtn.setDescription("Legend");
        legendLayoutBtn.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            legendLayout.setPopupVisible(true);
        });

        bottomRightPanel.addComponent(selectAllBtn);
        bottomRightPanel.setComponentAlignment(selectAllBtn, Alignment.TOP_CENTER);
        bottomRightPanel.addComponent(updateLayoutBtn);
        bottomRightPanel.setComponentAlignment(updateLayoutBtn, Alignment.TOP_CENTER);
        bottomRightPanel.addComponent(legendLayoutBtn);
        bottomRightPanel.setComponentAlignment(legendLayoutBtn, Alignment.TOP_CENTER);
        bottomRightPanel.addStyleName("righttbottompanelstyle");
        mainContainer.addComponent(legendLayout, "left: " + 50 + "%; top: " + 50 + "%");
        lefTtopPanel = new VerticalLayout();
        lefTtopPanel.setSpacing(false);
        lefTtopPanel.setWidthUndefined();
        lefTtopPanel.addStyleName("inframe");

        //init controls
        proteinsControl = new OptionGroup();
        proteinsControl.setMultiSelect(false);
        proteinsControl.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        proteinsControl.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        proteinsControl.addStyleName("smallertext");
        proteinsControl.addItem("Validation");
        proteinsControl.addItem("Modifications");
        proteinsControl.addItem("Protein Evidence");
        proteinsControl.addItem("PSMNumber");
        proteinsControl.setItemCaption("PSMNumber", "#PSMs");
        proteinsControl.addItem("Intensity");
        lefTtopPanel.addComponent(proteinsControl);
        lefTtopPanel.addStyleName("lefttoppanelstyle");

        proteinsControlListener = (Property.ValueChangeEvent event) -> {
            if (proteinsControl.getValue() == null) {
                return;
            }
            informationLegend.updateLegend(proteinsControl.getValue() + "");
            updateNodeColourType(proteinsControl.getValue() + "");
            if (intensityColorScaleLayout != null) {
                intensityColorScaleLayout.setVisible((proteinsControl.getValue() + "").equalsIgnoreCase("Intensity"));
            }
            psmColorScaleLayout.setVisible((proteinsControl.getValue() + "").equalsIgnoreCase("PSMNumber"));

//            scaleContainer.setVisible((proteinsControl.getValue() + "").equalsIgnoreCase("PSMNumber") || (proteinsControl.getValue() + "").equalsIgnoreCase("Intensity"));
        };
        proteinsControl.addValueChangeListener(proteinsControlListener);

        VerticalLayout leftBottomPanel = new VerticalLayout();
        leftBottomPanel.setSpacing(false);
        leftBottomPanel.setWidthUndefined();
        leftBottomPanel.addStyleName("inframe");
        leftBottomPanel.addStyleName("leftbottompanelstyle");
        nodeControl = new OptionGroup();
        nodeControl.setMultiSelect(true);
        nodeControl.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        nodeControl.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        nodeControl.addStyleName("smallertext");
        nodeControl.setDescription("Unique peptides only");
        nodeControl.addItem("Unique Only");
        nodeControl.addValueChangeListener((Property.ValueChangeEvent event) -> {
            uniqueOnly = nodeControl.getValue().toString().contains("Unique Only");
            selectNodes(selectedProteins.toArray());
            updateProteinsMode("Unique Only," + uniqueOnly);
        });
        leftBottomPanel.addComponent(nodeControl);
        //export button
        Button exportBtn = new Button(VaadinIcons.CAMERA);
        exportBtn.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        exportBtn.setWidth(25, Unit.PIXELS);
        exportBtn.setHeight(25, Unit.PIXELS);
        leftBottomPanel.addComponent(exportBtn);
        exportBtn.setDescription("Export graph image");
        exportBtn.addStyleName("exportcameraicon");

        SimpleFileDownloader downloader = new SimpleFileDownloader();
        addExtension(downloader);

        exportBtn.addClickListener((Button.ClickEvent event) -> {
            StreamResource graphStreamResource = generateImg(liveWidth + 20, liveHeight + 20);
            downloader.setFileDownloadResource(graphStreamResource);
            downloader.download();

        });

        //calculate graph
        edgesImage = new Image();
        GraphComponent.this.edgesImage.setSizeFull();

        //draw graph     
        canvas = new AbsoluteLayout();
        GraphComponent.this.canvas.setSizeFull();
        GraphComponent.this.setSizeFull();
        sizeReporter = new SizeReporter(GraphComponent.this.canvas);
        sizeReporter.addResizeListener((ComponentResizeEvent event) -> {
            int tWidth = event.getWidth();
            int tHeight = event.getHeight();

            if (tWidth < 100 || tHeight < 100 || (Math.abs(tWidth - liveWidth) < 5 && Math.abs(tHeight - liveHeight) < 5)) {
                return;
            }
            if (liveWidth == tWidth && liveHeight == tHeight) {
                return;
            }

            liveWidth = tWidth;
            liveHeight = tHeight;
            updateGraphLayout();
        });

        mainContainer.addStyleName("graphframeframe");
// Wrap the layout to allow handling drops
        DragAndDropWrapper layoutWrapper
                = new DragAndDropWrapper(canvas);

        GraphComponent.this.addComponent(mainContainer);
        layoutWrapper.setSizeFull();
// Handle moving components within the AbsoluteLayout

        dropHandler = new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {

                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                edgesImage.addStyleName("hide");
                Component component = event.getTransferable().getSourceComponent();
                if (component instanceof WrappedComponent) {
                    WrappedComponent node = (WrappedComponent) component;
                    WrapperTransferable t = (WrapperTransferable) event.getTransferable();
                    WrapperTargetDetails details = (WrapperTargetDetails) event.getTargetDetails();
                    // Calculate the drag coordinate difference
                    int xChange = details.getMouseEvent().getClientX() - t.getMouseDownEvent().getClientX();
                    int yChange = details.getMouseEvent().getClientY() - t.getMouseDownEvent().getClientY();
                    // Move the component in the absolute layout
                    ComponentPosition pos = canvas.getPosition(t.getSourceComponent());
                    pos.setLeftValue(pos.getLeftValue() + xChange);
                    pos.setTopValue(pos.getTopValue() + yChange);

                    double x = pos.getLeftValue();
                    double y = pos.getTopValue();
                    nodesMap.get(node.getData() + "").setX(x);
                    nodesMap.get(node.getData() + "").setY(y);
                    drawEdges();
                }

            }
        };
        layoutWrapper.setDropHandler(dropHandler);

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setSizeFull();
        wrapper.setMargin(new MarginInfo(true, true, true, true));
        wrapper.addComponent(edgesImage);

        proteinPeptideGraphWrapper = new AbsoluteLayout() {
            @Override
            public void setVisible(boolean visible) {
                super.setVisible(visible);
                bottomRightPanel.setVisible(visible);
                lefTtopPanel.setVisible(visible);
                leftBottomPanel.setVisible(visible);
                graphInfo.setVisible(visible);

            }

        };
        proteinPeptideGraphWrapper.setSizeFull();
        proteinPeptideGraphWrapper.addComponent(wrapper, "left: 0px; top: 0px");
        mainContainer.addComponent(bottomRightPanel, "right: " + 10 + "px; bottom: " + -12 + "px");
        mainContainer.addComponent(lefTtopPanel, "left: " + 10 + "px; top: " + -12 + "px");
        mainContainer.addComponent(rightBottomPanel, "right: " + 15 + "px; top: " + 15 + "px");
        mainContainer.addComponent(leftBottomPanel, "left: " + 10 + "px; bottom: " + -12 + "px");
        wrapper = new VerticalLayout();
        wrapper.setSizeFull();
        wrapper.setMargin(new MarginInfo(true, true, true, true));
        wrapper.addComponent(layoutWrapper);
        proteinPeptideGraphWrapper.addComponent(wrapper, "left: 0px; top: 0px");
        mainContainer.addComponent(proteinPeptideGraphWrapper, "left: 0px; top: 0px");

        this.proteoformLayerContainer = new AbsoluteLayout();
        this.proteoformLayerContainer.setSizeFull();
        mainContainer.addComponent(proteoformLayerContainer);
        proteoformLayerContainer.setVisible(false);

        VerticalLayout middleBottomPanel = new VerticalLayout();
        middleBottomPanel.setSpacing(false);
        middleBottomPanel.setWidthUndefined();
        middleBottomPanel.addStyleName("inframe");
        middleBottomPanel.addStyleName("highlightedoptions");
        //init controls

        graphsControl = new OptionGroup();
        graphsControl.setMultiSelect(false);
        graphsControl.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        graphsControl.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        graphsControl.addStyleName("smallertext");
        graphsControl.addStyleName("graphcontrolstyle");

        graphsControl.addItem("Protein-Peptide");
        graphsControl.addItem("Proteoform");
        middleBottomPanel.addComponent(graphsControl);
        mainContainer.addComponent(middleBottomPanel, "right: 170px; bottom: " + -12 + "px");
        graphsControl.setValue("Protein-Peptide");
        Property.ValueChangeListener graphsControlListener = (Property.ValueChangeEvent event) -> {
            if (proteoformLayerContainer.getComponentCount() == 0) {
                graphsControl.setValue("Protein-Peptide");
                return;
            }
            lastSelected = proteinsControl.getValue();
            proteoformLayerContainer.setVisible(graphsControl.getValue().toString().equalsIgnoreCase("Proteoform"));
            proteinPeptideGraphWrapper.setVisible(!proteoformLayerContainer.isVisible());
            if (proteoformLayerContainer.isVisible()) {
                updateProteinsMode("Proteoform");
                selectedItem(selectedProteins, selectedPeptides, graphsControl.getValue().toString().equalsIgnoreCase("Proteoform"));
            } else {
                proteinsControl.setValue(lastSelected);
                updateProteinsMode("Protein-Peptide");
                updateProteinsMode(lastSelected.toString());
            }
        };
        graphsControl.addValueChangeListener(graphsControlListener);
        scaleContainer = new AbsoluteLayout();
        scaleContainer.setWidth(8, Unit.PIXELS);
        scaleContainer.setHeight(150, Unit.PIXELS);
        proteinPeptideGraphWrapper.addComponent(scaleContainer, "left: 20px; top: 30px");
        scaleContainer.setStyleName("colorscalecontainer");

    }

    public String getThumbImgeUrl() {
        return thumbImgeUrl;
    }

    public void addProteoformGraphComponent(NetworkGraphComponent proteinsPathwayNewtorkGraph) {
        this.proteinsPathwayNewtorkGraph = proteinsPathwayNewtorkGraph;
        proteoformLayerContainer.addComponent(proteinsPathwayNewtorkGraph);

    }

    private void updateGraphLayout() {
        if (graph == null) {
            return;
        }
        ScalingControl scaler = new CrossoverScalingControl();
        graphLayout = new FRLayout<>(graph);
        visualizationViewer = new VisualizationViewer<>(graphLayout, new Dimension(liveWidth, liveHeight));
        scaler.scale(visualizationViewer, 0.9f, visualizationViewer.getCenter());
        reDrawGraph();
    }

    private void updateNodeColourType(String modeType) {
        lastSelectedModeType = modeType;
        nodesMap.keySet().forEach((key) -> {
            nodesMap.get(key).setNodeStatues(modeType);
        });
        updateProteinsMode(modeType);

    }

    public void updateGraphData(ProteinGroupObject selectedProtein, Map<String, ProteinGroupObject> proteinNodes, Map<String, PeptideObject> peptidesNodes, HashMap<String, ArrayList<String>> edges, RangeColorGenerator psmColorScale, boolean quantDs, RangeColorGenerator intensityColorScale) {
        uniqueOnly = nodeControl.getValue().equals("Unique Only");
        canvas.removeAllComponents();
        nodesMap.clear();
        edgesMap.clear();
        selectedProteins.clear();
        selectedPeptides.clear();
        //calculate graph
        if (selectedProtein == null) {
            graphInfo.setValue("#Proteins: <font style='float:right;padding-left: 3px;'>0</font><br/>#Peptides: <font style='float:right;padding-left: 3px;'>0</font>");
            return;
        }
        this.proteinNodes = proteinNodes;
        this.peptidesNodes = peptidesNodes;

        this.edges = edges;
        setUpGraph();
        if (!quantDs) {
            proteinsControl.removeItem("Intensity");
        }
        if (graph == null || graph.getVertices() == null) {
            System.out.println("at error o catch ");
            return;
        }
        graph.getVertices().forEach((node) -> {
            String modificationsAsString = "";
            ModificationMatch[] modifications = null;
            String sequence = "";
            int psmNumber;
            double intinsity;
            String intinsityColor;
            String tooltip;
            if (peptidesNodes.containsKey(node)) {
                modificationsAsString = peptidesNodes.get(node).getVariableModificationsAsString();
                modifications = peptidesNodes.get(node).getVariableModifications();
                sequence = peptidesNodes.get(node).getSequence();
                tooltip = node;
                psmNumber = peptidesNodes.get(node).getPSMsNumber();

                intinsity = peptidesNodes.get(node).getIntensity();
                intinsityColor = peptidesNodes.get(node).getIntensityColor();

                peptidesNodes.get(node).setPsmColor(psmColorScale.getColor(psmNumber));
            } else {
                psmNumber = -1;
                tooltip = node + "<br/>" + proteinNodes.get(node).getDescription();

                intinsity = proteinNodes.get(node).getAllPeptidesIntensity();
                intinsityColor = proteinNodes.get(node).getAllPeptideIintensityColor();

            }

            String[] intColoArr = intinsityColor.toUpperCase().replace("RGB(", "").replace(")", "").split(",");
            Color intCol = Color.lightGray;
            if (intColoArr.length == 3) {
                intCol = new Color(Integer.valueOf(intColoArr[0]), Integer.valueOf(intColoArr[1]), Integer.valueOf(intColoArr[2]));
            }

            Node n = new Node(node, tooltip, modifications, sequence, psmNumber, psmColorScale.getAWTColor(psmNumber), intinsity, intCol) {
                @Override
                public void selected(String id) {
                    selectNodes(new Object[]{id});

                }
            };
            informationLegend.updateModificationLayout(modificationsAsString);
            this.proteinsPathwayNewtorkGraph.getInformationLegend().updateModificationLayout(modificationsAsString);
            n.setX(graphLayout.getX(node));
            n.setY(graphLayout.getY(node));
            nodesMap.put(node, n);
            if (peptidesNodes.containsKey(node)) {
                n.setDefaultStyleName("peptidenode");
                if (selectedProtein.isRelatedPeptide(node)) {
                    n.setSelected(true);
                }
                n.setType(1);
                n.setValidationStatuesStyle(styles.get(peptidesNodes.get(node).getValidation()));
                n.setProteinEvidenceStyle(styles.get("Not Applicable"));
                n.setEvidenceColor(Color.lightGray);
                if (peptidesNodes.get(node).getValidation().equalsIgnoreCase("Confident")) {
                    n.setValidationColor(new Color(4, 180, 95));
                } else if (peptidesNodes.get(node).getValidation().equalsIgnoreCase("doubtful")) {
                    n.setValidationColor(new Color(255, 200, 0));
                } else {
                    n.setValidationColor(new Color(213, 8, 8));
                }

            } else {
                if (proteinNodes.containsKey(node) && proteinNodes.get(node) != null) {
                    n.setValidationStatuesStyle(styles.get(proteinNodes.get(node).getValidation()));
                    n.setProteinEvidenceStyle(styles.get(proteinNodes.get(node).getProteinEvidence()));
                    if (proteinNodes.get(node).getValidation() == null) {
                    } else if (proteinNodes.get(node).getValidation().equalsIgnoreCase("Confident")) {
                        n.setValidationColor(new Color(4, 180, 95));
                    } else if (proteinNodes.get(node).getValidation().equalsIgnoreCase("doubtful")) {
                        n.setValidationColor(new Color(255, 200, 0));
                    } else {
                        n.setValidationColor(new Color(213, 8, 8));
                    }
                    if (proteinNodes.get(node).getProteinEvidence() == null) {

                    } else if (proteinNodes.get(node).getProteinEvidence().equalsIgnoreCase("protein")) {
                        n.setEvidenceColor(new Color(133, 202, 133));
                    } else if (proteinNodes.get(node).getProteinEvidence().equalsIgnoreCase("transcript")) {
                        n.setEvidenceColor(new Color(210, 177, 117));
                    } else if (proteinNodes.get(node).getProteinEvidence().equalsIgnoreCase("homology")) {
                        n.setEvidenceColor(new Color(39, 119, 133));
                    } else if (proteinNodes.get(node).getProteinEvidence().equalsIgnoreCase("predicted")) {
                        n.setEvidenceColor(new Color(213, 172, 230));
                    } else if (proteinNodes.get(node).getProteinEvidence().equalsIgnoreCase("uncertain")) {
                        n.setEvidenceColor(new Color(233, 0, 0));
                    }

                } else {
                    n.setValidationStatuesStyle(styles.get("Not Available"));
                    n.setProteinEvidenceStyle(styles.get("Not Available"));
                }

                n.setDefaultStyleName("proteinnode");
                n.setType(0);
                if (selectedProtein.getProteinGroupSet().contains(node)) {
                    n.setSelected(true);
                }
            }

            final WrappedComponent wrapper = new WrappedComponent(n,
                    dropHandler);
            wrapper.setSizeUndefined();
            wrapper.setData(node);
            wrapper.setDescription(n.getDescription());
            canvas.addComponent(wrapper, "left: " + n.getX() + "px; top: " + n.getY() + "px");

        });
        Set<String> tnodesMap = new HashSet<>();
        edges.keySet().forEach((key) -> {
            ArrayList<String> edg = edges.get(key);
            edg.stream().map((node) -> {

                Node n1 = nodesMap.get(key);
                Node n2 = nodesMap.get(node);
                Edge edge = new Edge(n1, n2, !proteinNodes.get(node).isEnymaticPeptide(key));
                String edgeString = n1.getNodeId() + "__" + n2.getNodeId();
                String edgeString2 = n2.getNodeId() + "__" + n1.getNodeId();
                if (!tnodesMap.contains(edgeString) && !tnodesMap.contains(edgeString2)) {
                    n1.addEdge();
                    n2.addEdge();
                }
                tnodesMap.add(edgeString);
                tnodesMap.add(edgeString2);

                return edge;
            }).forEachOrdered((edge) -> {
                edgesMap.add(edge);
            });
        });
        selectNodes(selectedProtein.getProteinGroupSet().toArray());
        if (lastSelectedModeType == null) {
            lastSelectedModeType = ("PSMNumber");
        }
        thumbImgeUrl = generateThumbImg();
        if (psmColorScaleLayout != null) {
            scaleContainer.removeComponent(psmColorScaleLayout);
        }
        this.psmColorScaleLayout = psmColorScale.getVerticalScale(true);

        informationLegend.updatePSMNumberLayout(psmColorScale.getColorScale());

        scaleContainer.addComponent(psmColorScaleLayout);
        if (quantDs && intensityColorScale != null) {
            if (intensityColorScaleLayout != null) {
                scaleContainer.removeComponent(intensityColorScaleLayout);
            }
            this.intensityColorScaleLayout = intensityColorScale.getVerticalScale(true);
            scaleContainer.addComponent(intensityColorScaleLayout);
            intensityColorScaleLayout.setVisible(false);
            informationLegend.updateIntensityLayout(intensityColorScale.getColorScale());
        }

    }

    private void reDrawGraph() {
        if (visualizationViewer != null) {
            edgesImage.addStyleName("hide");
            setUpGraph();
            Iterator<Component> itr = canvas.iterator();
            while (itr.hasNext()) {
                Component component = itr.next();
                if (component instanceof WrappedComponent) {
                    WrappedComponent wComp = (WrappedComponent) component;
                    String nodeName = wComp.getData() + "";
                    AbsoluteLayout.ComponentPosition newPosition = canvas.getPosition(wComp);
                    double x = graphLayout.getX(nodeName);
                    double y = graphLayout.getY(nodeName);
                    nodesMap.get(nodeName).setX(x);
                    nodesMap.get(nodeName).setY(y);
                    newPosition.setCSSString("left: " + x + "px; top: " + y + "px");
                }
            }
            drawEdges();
        }
    }

    private void selectNodes(Object[] ids) {
        redrawSelection(ids, true);
        selectedItem(selectedProteins, selectedPeptides, graphsControl.getValue().toString().equalsIgnoreCase("Proteoform"));
    }

    private void selectAll() {
        nodeControl.setValue(null);
        selectNodes(nodesMap.keySet().toArray());

    }

    public void updateMode() {
        if (lastSelectedModeType != null) {
            proteinsControl.setValue(null);
            proteinsControl.setValue(lastSelectedModeType);
            graphsControl.setValue("Protein-Peptide");
        }
    }

    /**
     * Set up the graph.
     *
     * @param parentPanel the parent panel
     * @return the visualisation viewer
     */
    private void setUpGraph() {
        graph = new UndirectedSparseGraph<>();
        // add all the nodes
        proteinNodes.keySet().forEach((node) -> {
            graph.addVertex(node);
        });
        peptidesNodes.keySet().forEach((node) -> {
            graph.addVertex(node);
        });

        // add the vertexes
        Iterator<String> startNodeKeys = edges.keySet().iterator();
        while (startNodeKeys.hasNext()) {
            String startNode = startNodeKeys.next();
            edges.get(startNode).forEach((endNode) -> {
                graph.addEdge(startNode + "|" + endNode, startNode, endNode);
            });
        }
        // create the visualization viewer
        graphLayout = new FRLayout<>(graph);
        visualizationViewer = new VisualizationViewer<>(graphLayout, new Dimension(liveWidth, liveHeight));
    }

    private void drawEdges() {
        if (liveWidth < 1 || liveHeight < 1) {
            return;
        }
        BufferedImage image = new BufferedImage(liveWidth, liveHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        edgesMap.forEach((edge) -> {
            Shape edgeLine = drawEdge((int) edge.getStartX(), (int) edge.getStartY(), (int) edge.getEndX(), (int) edge.getEndY());
            if (uniqueOnly && edge.isHide()) {
                g2.setPaint(Color.lightGray);
            } else if (edge.isSelected()) {
                g2.setPaint(Color.GRAY);
            } else {
                g2.setPaint(Color.LIGHT_GRAY);
            }
            if (edge.isDotted()) {
                g2.draw(dashLineStroke.createStrokedShape(edgeLine));
            } else {
                g2.draw(edgeLine);
            }
        });

        g2.dispose();
        byte[] imageData = null;
        try {
            ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
            imageData = in.encode(image);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

        String base64 = Base64.encodeBytes(imageData);
        base64 = "data:image/png;base64," + base64;
        edgesImage.setSource(new ExternalResource(base64));
        edgesImage.removeStyleName("hide");

    }

    private String generateThumbImg() {
        FRLayout tgraphlayout = new FRLayout<>(graph);
        VisualizationViewer vv = new VisualizationViewer<>(tgraphlayout, new Dimension(80, 80));
        ScalingControl scaler = new CrossoverScalingControl();
        scaler.scale(vv, 0.9f, vv.getCenter());

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setPaint(Color.GRAY);
        graph.getEdges().stream().map((edge) -> drawLine((int) tgraphlayout.getX(graph.getEndpoints(edge).getFirst()) + 10, (int) tgraphlayout.getY(graph.getEndpoints(edge).getFirst()) + 10, (int) tgraphlayout.getX(graph.getEndpoints(edge).getSecond()) + 10, (int) tgraphlayout.getY(graph.getEndpoints(edge).getSecond()) + 10)).forEachOrdered((line) -> {
            g2.draw(line);
        });
        graph.getVertices().stream().map((node) -> {
            Shape circle;
            if (peptidesNodes.containsKey(node)) {
                g2.setPaint(Color.GRAY);
                g2.setPaint(new Color(25, 125, 225));

                circle = new Ellipse2D.Double((int) tgraphlayout.getX(node) - 5 + 10, tgraphlayout.getY(node) - 5 + 10, 10, 10);
            } else {
                g2.setPaint(Color.LIGHT_GRAY);
                g2.setPaint(new Color(233, 0, 0));

                circle = new Ellipse2D.Double((int) tgraphlayout.getX(node) - 10 + 10, tgraphlayout.getY(node) - 10 + 10, 20, 20);
            }
            return circle;
        }).map((circle) -> {
            g2.fill(circle);
            return circle;
        }).forEachOrdered((circle) -> {
            g2.setPaint(Color.WHITE);
            g2.draw(circle);
        });

        g2.dispose();
        byte[] imageData = null;
        try {
            ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
            imageData = in.encode(image);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

        String base64 = Base64.encodeBytes(imageData);
        base64 = "data:image/png;base64," + base64;
        return base64;

    }

    private StreamResource generateImg(int width, int height) {
//        FRLayout tgraphlayout = new FRLayout<>(graph);
//        VisualizationViewer vv = new VisualizationViewer<>(tgraphlayout, new Dimension(width - 20, height - 20));
//        ScalingControl scaler = new CrossoverScalingControl();
//        scaler.scale(vv, 0.9f, vv.getCenter());

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2d.setPaint(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        g2d.setPaint(Color.WHITE);
        g2d.fillRect(2, 2, width - 4, height - 4);

        g2d.setPaint(Color.GRAY);
        graph.getEdges().stream().map((edge) -> drawLine((int) graphLayout.getX(graph.getEndpoints(edge).getFirst()) + 20, (int) graphLayout.getY(graph.getEndpoints(edge).getFirst()) + 20, (int) graphLayout.getX(graph.getEndpoints(edge).getSecond()) + 20, (int) graphLayout.getY(graph.getEndpoints(edge).getSecond()) + 20)).forEachOrdered((line) -> {
            g2d.draw(line);
        });
        graph.getVertices().stream().map((node) -> {
            Shape circle;
            g2d.setPaint(Color.BLACK);
            if (peptidesNodes.containsKey(node)) {
                g2d.setPaint(nodesMap.get(node).getCurrentActiveColor());

                circle = new Ellipse2D.Double((int) graphLayout.getX(node) - 10 + 20, graphLayout.getY(node) - 10 + 20, 20, 20);
                g2d.setPaint(nodesMap.get(node).getCurrentActiveColor());
            } else {
//
                circle = new Ellipse2D.Double((int) graphLayout.getX(node) - 20 + 20, graphLayout.getY(node) - 20 + 20, 40, 40);
                g2d.setPaint(nodesMap.get(node).getCurrentActiveColor());
            }
            return circle;
        }).map((circle) -> {

            g2d.fill(circle);
            return circle;
        }).forEachOrdered((circle) -> {
            g2d.setPaint(Color.BLACK);
            g2d.draw(circle);
        });

        g2d.dispose();

        /**
         * **
         */
//        Dimension original = image.getSize();
//
//        System.out.println("Original = " + original);
//
//        int width = tp.getWidth() * 4;
//        int height = tp.getHeight() * 4;
//
//        System.out.println("Target = " + width + "x" + height);
//        tp.setSize(width, height);
//        tp.doLayout();
//        tp.print(g2d);
        g2d.dispose();
//
//        tp.setSize(original);
//        tp.doLayout();

        /**
         * **
         */
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bos);
            final byte[] touseImageData = bos.toByteArray();
            StreamResource graphAsImageResource = new StreamResource(() -> {
                return new ByteArrayInputStream(touseImageData);
            }, "graph.png");
            return graphAsImageResource;
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return null;

    }

    private Shape drawLine(int x1, int y1, int x2, int y2) {
        return new Line2D.Double(x1, y1, x2, y2);
    }

    private Shape drawEdge(int x1, int y1, int x2, int y2) {
        return new Line2D.Double(x1, y1, x2, y2);
    }

    public abstract void selectedItem(Set<Object> selectedParentItems, Set<Object> selectedChildsItems, boolean updateProteform);

    public abstract void updateProteinsMode(String modeType);

    public void selectChildItem(Object parentId, Object childId) {
        redrawSelection(new Object[]{parentId, childId}, false);

    }

    public void selectParentItem(Object parentId) {
        redrawSelection(new Object[]{parentId}, true);
    }

    private void redrawSelection(Object[] ids, boolean selectRelatedNodes) {
        edgesImage.addStyleName("hide");
        nodesMap.values().forEach((node) -> {
            node.setSelected(false);
            node.setUniqueOnlyMode(uniqueOnly);
        });
        this.selectedPeptides.clear();
        this.selectedProteins.clear();
        for (Object id : ids) {

            Node n = nodesMap.get(id);
            if (n == null) {
                continue;
            }
            if (uniqueOnly && n.getType() == 1) {
                n.setSelected(true);
                selectedPeptides.add(id);
            } else if (n.getType() == 0) {
                n.setSelected(true);
                selectedProteins.add(id);
            } else if (n.getType() == 1) {
                n.setSelected(true);
                selectedPeptides.add(id);
            }

            if (selectRelatedNodes) {
                edgesMap.stream().map((edge) -> {
                    edge.select(n, uniqueOnly);
                    return edge;
                }).map((edge) -> {
                    if (edge.getN2().isSelected()) {
                        selectedProteins.add(edge.getN2().getNodeId());
                    }
                    return edge;
                }).filter((edge) -> (edge.getN1().isSelected())).forEachOrdered((edge) -> {
                    selectedPeptides.add(edge.getN1().getNodeId());
                });
            }
        }
        drawEdges();
        graphInfo.setValue("#Proteins: <font style='float:right;padding-left: 3px;'>" + selectedProteins.size() + "</font><br/>#Peptides: <font style='float:right;padding-left: 3px;'>" + selectedPeptides.size() + "</font>");
    }

    public Set<Object> getSelectedProteins() {
        return selectedProteins;
    }

    public Set<Object> getSelectedPeptides() {
        return selectedPeptides;
    }

    public void setEnablePathway(boolean enable) {
//        enable = false;
        graphsControl.setItemEnabled("Proteoform", enable);
        if (!enable) {
            graphsControl.setValue("Protein-Peptide");
        }
    }

    /**
     * This class is a wrapper for the dropped component that is used in the
     * Drag-Drop layout.
     *
     * @author Yehia Farag
     */
    class WrappedComponent extends DragAndDropWrapper {

        /**
         * The layout drop handler.
         */
        private final DropHandler dropHandler;

        /**
         * Constructor to initialise the main attributes.
         *
         * @param content     the dropped component (the label layout)
         * @param dropHandler The layout drop handler.
         */
        public WrappedComponent(final Component content, final DropHandler dropHandler) {
            super(content);
            this.dropHandler = dropHandler;
            WrappedComponent.this.setDragStartMode(DragAndDropWrapper.DragStartMode.WRAPPER);
        }

        @Override
        public DropHandler getDropHandler() {
            return dropHandler;
        }

    }
}
