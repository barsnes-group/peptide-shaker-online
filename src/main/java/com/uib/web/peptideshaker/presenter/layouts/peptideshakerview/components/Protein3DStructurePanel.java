package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.model.core.pdb.ChainBlock;
import com.uib.web.peptideshaker.model.core.pdb.PDBMatch;
import com.uib.web.peptideshaker.model.core.pdb.PdbHandler;
import com.vaadin.data.Property;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.*;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * This class represents 3D protein structure panel using JSMOL web service
 *
 * @author Yehia Farag
 */
public class Protein3DStructurePanel extends AbsoluteLayout {

    private final VerticalLayout LiteMolPanel;
    private final LiteMOL3DComponent liteMOL3DComponent;
    private final Map<String, List<HashMap<String, Object>>> peptidesQueryMap;
    private final ComboBox pdbMatchesSelect;
    private final ComboBox pdbChainsSelect;
    private final Property.ValueChangeListener pdbMatchSelectlistener;
    private final Property.ValueChangeListener pdbChainsSelectlistener;
    private final int[] defaultSelectedBlueColor = new int[]{25, 125, 255};
    private final int[] defaultUnSelectedBlueColor = new int[]{132, 191, 249};
    private final int[] defaultSelectedConfidentColor = new int[]{0, 128, 0};
    private final int[] defaultUnselectedConfidenColor = new int[]{159, 242, 159};
    private final int[] defaultSelectedDoubtfulColor = new int[]{253, 167, 8};
    private final int[] defaultUnselectedDoubtfulColor = new int[]{255, 224, 192};
    private final int[] defaultSelectedNotValidatedColor = new int[]{233, 0, 0};
    private final int[] defaultUnselectedNotValidatedColor = new int[]{233, 146, 146};
    private final int[] defaultSelectedNotAvailableColor = new int[]{211, 211, 211};
    private final int[] defaultUnselectedNotAvailableColor = new int[]{246, 246, 246};
    private final int[] basicColor = new int[]{226, 226, 226};
    private final Map<String, PeptideObject> proteinPeptidesMap;
    private final Map<String, List<ChainBlock>> pdbBlockMap;
    private final Label uniprotLabel;
    private final PdbHandler pdbHandler = new PdbHandler();
    /**
     * The post translational modifications factory.
     */
    private final ModificationFactory PTM = ModificationFactory.getInstance();
    private AbsoluteLayout chainCoverageLayout;
    private int moleculeMode = 1;
    private ChainCoverageComponent lastSelectedChainCoverage;
    private Object lastSelectedAccession;
    private String lastSelectedProteinSequence;
    private String lastSelectedPeptideKey;
    private Collection<PeptideObject> proteinPeptides;
    private int proteinSequenceLength;
    private PDBMatch lastSelectedMatch;

    public Protein3DStructurePanel() {
        Protein3DStructurePanel.this.setSizeFull();
        Protein3DStructurePanel.this.setStyleName("proteinStructiorpanel");

        this.pdbBlockMap = new HashMap<>();
        this.peptidesQueryMap = new LinkedHashMap<>();
        this.proteinPeptidesMap = new LinkedHashMap<>();

        LiteMolPanel = new VerticalLayout();
        LiteMolPanel.setSizeFull();

        liteMOL3DComponent = new LiteMOL3DComponent();
        liteMOL3DComponent.setSizeFull();
        liteMOL3DComponent.setVisible(true);

        chainCoverageLayout = new AbsoluteLayout() {
            @Override
            public void setVisible(boolean v) {
                if (this.getParent() != null) {
                    this.getParent().setVisible(v);
                }
                super.setVisible(v);
            }
        };
        chainCoverageLayout.setSizeFull();
        LiteMolPanel.addComponent(liteMOL3DComponent);
        Protein3DStructurePanel.this.addComponent(LiteMolPanel);

        uniprotLabel = new Label("UniProt:");
        uniprotLabel.addStyleName("selectchain3dMenue");
        uniprotLabel.setVisible(false);

        pdbChainsSelect = new ComboBox("Chains:");
        pdbChainsSelect.setTextInputAllowed(false);
        pdbChainsSelect.addStyleName("select3dMenue");
        pdbChainsSelect.addStyleName("selectchain3dMenue");
        pdbChainsSelect.setNullSelectionAllowed(false);
        pdbChainsSelect.setVisible(false);
        HorizontalLayout dropdownMenuesContainer = new HorizontalLayout();
        dropdownMenuesContainer.setWidth(321, Unit.PIXELS);
        dropdownMenuesContainer.setHeight(25, Unit.PIXELS);
        dropdownMenuesContainer.setStyleName("select3dMenue");

        Protein3DStructurePanel.this.addComponent(dropdownMenuesContainer, "left: 10px; bottom:-12.5px");
        this.pdbChainsSelectlistener = ((Property.ValueChangeEvent event) -> {
            LiteMolPanel.setVisible(pdbChainsSelect.getValue() != null);
            if (LiteMolPanel.isVisible()) {
                lastSelectedChainCoverage.selectChain(pdbChainsSelect.getValue() + "");
                chainCoverageLayout.removeAllComponents();
                chainCoverageLayout.addComponent(lastSelectedChainCoverage.getChainCoverageWebComponent());
                contsructQueries(pdbBlockMap.get(pdbChainsSelect.getValue() + ""));
                selectPeptides(lastSelectedPeptideKey);

            }
        });
        pdbChainsSelect.addValueChangeListener(pdbChainsSelectlistener);
        pdbMatchesSelect = new ComboBox("PDB:") {
            @Override
            public void setVisible(boolean visible) {
                uniprotLabel.setVisible(visible);
                super.setVisible(visible);
            }

        };
        pdbMatchesSelect.setTextInputAllowed(false);
        pdbMatchesSelect.addStyleName("select3dMenue");
        pdbMatchesSelect.setNullSelectionAllowed(false);
        pdbMatchesSelect.setCaptionAsHtml(true);
        pdbMatchesSelect.setVisible(false);
        dropdownMenuesContainer.addComponent(uniprotLabel);
        dropdownMenuesContainer.setExpandRatio(uniprotLabel, 115);
        dropdownMenuesContainer.addComponent(pdbMatchesSelect);
        dropdownMenuesContainer.setExpandRatio(pdbMatchesSelect, 108);
        dropdownMenuesContainer.addComponent(pdbChainsSelect);
        dropdownMenuesContainer.setExpandRatio(pdbChainsSelect, 98);

        this.pdbMatchSelectlistener = ((Property.ValueChangeEvent event) -> {
            //time to active d3
            pdbChainsSelect.removeValueChangeListener(pdbChainsSelectlistener);
            pdbChainsSelect.removeAllItems();
            LiteMolPanel.setVisible(pdbMatchesSelect.getValue() != null);//
            lastSelectedMatch = pdbHandler.updatePdbInformation(pdbMatchesSelect.getValue().toString(), lastSelectedProteinSequence, lastSelectedAccession);
            pdbBlockMap.clear();
            lastSelectedChainCoverage = reCalculateChainRange(lastSelectedMatch.getChains(), proteinSequenceLength);
            pdbChainsSelect.addItem("All");
            pdbChainsSelect.setItemCaption("All", "All");
            pdbChainsSelect.setItemIcon("All", new ExternalResource(lastSelectedChainCoverage.selectChain("All")));
            pdbBlockMap.put("All", new ArrayList<>());

            lastSelectedMatch.getChains().stream().map((chain) -> {
                if (!pdbBlockMap.containsKey(chain.getChain_id())) {
                    List<ChainBlock> blocks = new ArrayList<>();
                    pdbBlockMap.put(chain.getChain_id(), blocks);
                }
                return chain;
            }).map((chain) -> {
                pdbChainsSelect.addItem(chain.getChain_id());
                return chain;
            }).map((chain) -> {
                pdbChainsSelect.setItemCaption(chain.getChain_id(), chain.getChain_id());
                return chain;
            }).map((chain) -> {
                pdbChainsSelect.setItemIcon(chain.getChain_id(), new ExternalResource(lastSelectedChainCoverage.selectChain(chain.getChain_id())));
                return chain;
            }).map((chain) -> {
                pdbBlockMap.get(chain.getChain_id()).add(chain);
                return chain;
            }).forEachOrdered((chain) -> {
                pdbBlockMap.get("All").add(chain);
            });
            if (pdbChainsSelect.getItemIds().size() == 2) {
                pdbBlockMap.remove("All");
                pdbChainsSelect.removeItem("All");
            }
            pdbMatchesSelect.setDescription(pdbMatchesSelect.getItemCaption(pdbMatchesSelect.getValue()));
            pdbChainsSelect.addValueChangeListener(pdbChainsSelectlistener);
            pdbChainsSelect.setValue(pdbChainsSelect.getItemIds().iterator().next());

        });
        pdbMatchesSelect.addValueChangeListener(pdbMatchSelectlistener);

    }

    public void reset() {
        liteMOL3DComponent.reset3DView();
        pdbMatchesSelect.setVisible(false);
        pdbChainsSelect.setVisible(false);
        chainCoverageLayout.setVisible(false);
        uniprotLabel.setVisible(false);
        lastSelectedPeptideKey = null;
    }

    public void activate3DProteinView() {
        boolean activate = liteMOL3DComponent.activate3DProteinView();
        if (!activate) {
            reset();
            return;
        }

        pdbMatchesSelect.setVisible(true);
        pdbChainsSelect.setVisible(true);
        chainCoverageLayout.setVisible(true);
        uniprotLabel.setVisible(true);
    }

    public void redrawCanvas() {
        liteMOL3DComponent.redrawCanvas();
    }

    public Object getLastSelectedAccession() {
        return lastSelectedAccession;
    }

    public void updatePanel(Object accession, String proteinSequence, Collection<PeptideObject> proteinPeptides, boolean showNotifications) {
        this.lastSelectedAccession = accession;
        this.lastSelectedProteinSequence = proteinSequence;
        this.uniprotLabel.setValue("UniProt: " + lastSelectedAccession);
        this.proteinPeptides = proteinPeptides;
        loadData(lastSelectedAccession, lastSelectedProteinSequence, showNotifications);

    }

    private void loadData(Object accessionObject, String proteinSequence, boolean showNotifications) {
        if (accessionObject == null) {
            return;
        }
        pdbMatchesSelect.removeValueChangeListener(pdbMatchSelectlistener);
        pdbMatchesSelect.removeAllItems();
        pdbBlockMap.clear();
        String accession = accessionObject.toString();
        proteinSequenceLength = proteinSequence.length();
        Map<String, PDBMatch> pdbMachSet = pdbHandler.getData(accession);
        if (pdbMachSet != null && !pdbMachSet.isEmpty()) {
            pdbMachSet.keySet().forEach((str) -> {
                pdbMatchesSelect.addItem(str);
                pdbMatchesSelect.setItemCaption(str, str.toUpperCase() + " " + pdbMachSet.get(str).getDescription());

            });
            if (pdbMatchesSelect.getItemIds() == null) {
                if (showNotifications) {
                    Notification.show("No visualization available ", Notification.Type.TRAY_NOTIFICATION);
                }
                reset();
                return;
            }
            pdbMatchesSelect.setVisible(true);
            pdbChainsSelect.setVisible(pdbMatchesSelect.isVisible());
            chainCoverageLayout.setVisible(pdbMatchesSelect.isVisible());
            pdbMatchesSelect.addValueChangeListener(pdbMatchSelectlistener);
            String pdbMachSelectValue = pdbMatchesSelect.getItemIds().toArray()[0] + "";
            pdbMatchesSelect.setValue(pdbMachSelectValue);

        } else {
            if (showNotifications) {
                Notification.show("No visualization available ", Notification.Type.TRAY_NOTIFICATION);
            }
            reset();
        }

    }

    private ChainCoverageComponent reCalculateChainRange(Collection<ChainBlock> chainBlocks, int proteinSequenceLength) {
        ChainCoverageComponent chainCoverage = new ChainCoverageComponent(proteinSequenceLength);
        chainBlocks.forEach((chain) -> {
            chainCoverage.addChainRange(chain.getChain_id(), chain.getStart_author_residue_number(), chain.getEnd_author_residue_number());
        });
        return chainCoverage;

    }

    private void selectPeptides(String peptideKey) {
        lastSelectedPeptideKey = peptideKey;
        String chainId = (pdbChainsSelect.getValue() + "");
        LinkedHashSet<HashMap> entriesSet = new LinkedHashSet<>();
        //color chains
        if (!chainId.equalsIgnoreCase("All")) {
            List<ChainBlock> chains = pdbBlockMap.get(pdbChainsSelect.getValue() + "");
            if (chains == null) {
                return;
            }
            chains.stream().map((selectedBlock) -> {
                int start = selectedBlock.getStart_residue_number();
                int end = selectedBlock.getEnd_residue_number();//we need color               
                HashMap chainSeq = initSequenceMap(selectedBlock.getChain_id(), lastSelectedMatch.getEntity_id(selectedBlock.getChain_id()), start, end, "valid", false, null, selectedBlock.getUniprot_chain_sequence(), "", "");
                return chainSeq;
            }).map((chainSeq) -> {
                chainSeq.put("color", initColorMap(Color.WHITE));//selectedChainColor
                return chainSeq;
            }).forEachOrdered((chainSeq) -> {
                entriesSet.add(chainSeq);
            });
        }

        //set color based on protein overview radio btn
        switch (moleculeMode) {
            case 1:
                int[] defaultSelectedColor = null;
                int[] defaultUnselectedColor;
                if (peptideKey == null || peptideKey.contains("null")) {
                    defaultUnselectedColor = defaultSelectedBlueColor;
                } else {
                    defaultUnselectedColor = defaultUnSelectedBlueColor;
                    defaultSelectedColor = defaultSelectedBlueColor;
                }
                peptidesQueryMap.keySet().forEach((key) -> {
                    peptidesQueryMap.get(key).stream().map((peptide) -> {
                        peptide.put("color", initColorMap(defaultUnselectedColor));
                        return peptide;
                    }).forEachOrdered((peptide) -> {
                        entriesSet.add(peptide);
                    });
                });
                if (peptideKey != null && peptidesQueryMap.containsKey(peptideKey)) {
                    for (HashMap peptide : peptidesQueryMap.get(peptideKey)) {
                        if (chainId.contains("All") || chainId.equalsIgnoreCase(peptide.get("struct_asym_id").toString())) {
                            peptide.put("color", initColorMap(defaultSelectedColor));
                            entriesSet.add(peptide);
                        }
                    }
                }
                break;
            case 2: {
                int[] selectedConfidentColor;
                int[] selectedDoubtfulColor;
                int[] selectedNotValidatedColor;
                int[] selectedNotAvailableColor;
                if (peptideKey == null || peptideKey.contains("null")) {
                    selectedConfidentColor = defaultSelectedConfidentColor;
                    selectedDoubtfulColor = defaultSelectedDoubtfulColor;
                    selectedNotValidatedColor = defaultSelectedNotValidatedColor;
                    selectedNotAvailableColor = defaultSelectedNotAvailableColor;
                } else {
                    selectedConfidentColor = defaultUnselectedConfidenColor;
                    selectedDoubtfulColor = defaultUnselectedDoubtfulColor;
                    selectedNotValidatedColor = defaultUnselectedNotValidatedColor;
                    selectedNotAvailableColor = defaultUnselectedNotAvailableColor;
                }
                peptidesQueryMap.keySet().forEach((key) -> {
                    for (HashMap peptide : peptidesQueryMap.get(key)) {
                        switch (peptide.get("validation").toString()) {
                            case "Doubtful":
                                peptide.put("color", initColorMap(selectedDoubtfulColor));
                                break;
                            case "Confident":
                                peptide.put("color", initColorMap(selectedConfidentColor));
                                break;
                            case "Not Validated":
                                peptide.put("color", initColorMap(selectedNotValidatedColor));
                                break;
                            default:
                                peptide.put("color", initColorMap(selectedNotAvailableColor));
                                break;

                        }

                        entriesSet.add(peptide);
                    }
                });
                if (peptideKey != null && peptidesQueryMap.containsKey(peptideKey)) {
                    for (HashMap peptide : peptidesQueryMap.get(peptideKey)) {
                        if (chainId.contains("All") || chainId.equalsIgnoreCase(peptide.get("struct_asym_id").toString())) {
                            switch (peptide.get("validation").toString()) {
                                case "Doubtful":
                                    peptide.put("color", initColorMap(defaultSelectedDoubtfulColor));
                                    break;
                                case "Confident":
                                    peptide.put("color", initColorMap(defaultSelectedConfidentColor));
                                    break;
                                case "Not Validated":
                                    peptide.put("color", initColorMap(defaultSelectedNotValidatedColor));
                                    break;
                                default:
                                    peptide.put("color", initColorMap(defaultSelectedNotAvailableColor));
                                    break;
                            }
                            entriesSet.add(peptide);
                        }
                    }
                }
                break;
            }
            case 3: {

                Map<String, Color> peptideOverlappingMap = new HashMap<>();
                peptidesQueryMap.keySet().forEach((key) -> {
                    peptidesQueryMap.get(key).stream().filter((peptide) -> (chainId.contains("All") || chainId.equalsIgnoreCase(peptide.get("struct_asym_id").toString()))).map((peptide) -> {
                        ModificationMatch[] varModifications = ((ModificationMatch[]) peptide.get("modifications"));//               
                        if (!(boolean) peptide.get("modified")) {
                            /**
                             * case of select all*
                             */
                            if (peptideKey == null || peptideKey.contains("null")) {
                                peptide.put("color", initColorMap(new int[]{211, 211, 211}));
                            } else {
                                peptide.put("color", initColorMap(new int[]{245, 245, 245}));
                            }
                        } else if (varModifications.length > 0) {
                            Color c = Color.ORANGE;
                            Set<String> modificationNames = new HashSet<>();
                            for (ModificationMatch mach : varModifications) {
                                modificationNames.add(mach.getModification());
                            }
                            if (modificationNames.size() == 1) {
                                c = new Color(PTM.getDefaultColor(modificationNames.iterator().next()));

                            }

                            if (peptideKey == null || peptideKey.contains("null")) {
                                peptide.put("color", initColorMap(c));
                            } else {

                                if (c.brighter().toString().equalsIgnoreCase(c.toString())) {
                                    c = new Color(Math.max(c.getRed(), 100), Math.max(c.getGreen(), 100), Math.max(c.getBlue(), 100));
                                    peptide.put("color", initColorMap(c));
                                } else {
                                    c = c.brighter().brighter();
                                    peptide.put("color", initColorMap(c));
                                }
                            }
                            if (!peptideOverlappingMap.containsKey(peptide.get("sequence_key").toString())) {
                                peptideOverlappingMap.put(peptide.get("sequence_key").toString(), c);
                            } else {
                                if (!peptideOverlappingMap.get(peptide.get("sequence_key").toString()).toString().equalsIgnoreCase(c.toString())) {
                                    if (peptideKey == null || peptideKey.contains("null")) {
                                        peptideOverlappingMap.put(peptide.get("sequence_key").toString(), Color.ORANGE);
                                    } else {
                                        peptideOverlappingMap.put(peptide.get("sequence_key").toString(), Color.ORANGE.brighter().brighter());
                                    }
                                }
                            }
                        }
                        return peptide;
                    }).forEachOrdered((peptide) -> {
                        entriesSet.add(peptide);
                    });
                });
                entriesSet.stream().filter((pep) -> (peptideOverlappingMap.containsKey(pep.get("sequence_key").toString()))).forEachOrdered((pep) -> {
                    pep.put("color", initColorMap(peptideOverlappingMap.get(pep.get("sequence_key").toString())));
                });
                if (peptideKey != null && peptidesQueryMap.containsKey(peptideKey)) {
                    peptidesQueryMap.get(peptideKey).stream().filter((peptide) -> (chainId.contains("All") || chainId.equalsIgnoreCase(peptide.get("struct_asym_id").toString()))).map((peptide) -> {
                        if (!(boolean) peptide.get("modified")) {
                            peptide.put("color", initColorMap(Color.GRAY.darker()));
                        } else {
                            Color c = Color.ORANGE;
                            if (peptide.get("modifications").toString().split(",").length == 1) {
                                c = new Color(PTM.getColor(peptide.get("modifications").toString().trim()));
                            }
                            peptide.put("color", initColorMap(c));
                        }
                        return peptide;
                    }).forEachOrdered((peptide) -> {
                        entriesSet.add(peptide);
                    });
                }
                break;
            }

            case 4: {

                peptidesQueryMap.keySet().forEach((key) -> {
                    peptidesQueryMap.get(key).stream().map((peptide) -> {

                        if (peptideKey == null || peptideKey.contains("null")) {
                            int[] colorArr = new int[3];
                            String psmColor = peptide.get("psm").toString().substring(4, peptide.get("psm").toString().length() - 1);
                            int index = 0;
                            for (String c : psmColor.split(",")) {
                                colorArr[index++] = Integer.parseInt(c);
                            }
                            peptide.put("color", initColorMap(colorArr));

                        } else {
                            int[] colorArr = new int[3];
                            String psmColor = peptide.get("psm").toString().substring(4, peptide.get("psm").toString().length() - 1);
                            int index = 0;
                            for (String c : psmColor.split(",")) {
                                colorArr[index++] = Integer.parseInt(c);
                            }
                            Color unselected = new Color(colorArr[0], colorArr[1], colorArr[2]).brighter();

                            peptide.put("color", initColorMap(unselected));

                        }

                        return peptide;
                    }).forEachOrdered((peptide) -> {
                        entriesSet.add(peptide);
                    });

                });
                if (peptideKey != null && !peptideKey.contains("null") && peptidesQueryMap.containsKey(peptideKey)) {
                    peptidesQueryMap.get(peptideKey).stream().map((peptide) -> {
                        int[] colorArr = new int[3];
                        String psmColor = peptide.get("psm").toString().substring(4, peptide.get("psm").toString().length() - 1);
                        int index = 0;
                        for (String c : psmColor.split(",")) {
                            colorArr[index++] = Integer.parseInt(c);
                        }
                        peptide.put("color", initColorMap(colorArr));

                        return peptide;
                    }).forEachOrdered((peptide) -> {
                        entriesSet.add(peptide);
                    });
                }
                break;
            }

            case 5: {
                peptidesQueryMap.keySet().forEach((key) -> {
                    try {
                        peptidesQueryMap.get(key).stream().map((peptide) -> {

                            if (peptideKey == null || peptideKey.contains("null")) {

                                String intensityColor = peptide.get("intensity").toString().substring(4, peptide.get("intensity").toString().length() - 1);

                                if (intensityColor.contains("%")) {
                                    String[] colorArr = new String[3];
                                    int index = 0;
                                    for (String c : intensityColor.split(",")) {
                                        colorArr[index++] = c;
                                    }
                                    peptide.put("color", initColorMap(colorArr));
                                } else {
                                    int[] colorArr = new int[3];
                                    int index = 0;
                                    for (String c : intensityColor.split(",")) {
                                        colorArr[index++] = Integer.parseInt(c);
                                    }
                                    peptide.put("color", initColorMap(colorArr));
                                }

                            } else {

                                String intensityColor = peptide.get("intensity").toString().substring(4, peptide.get("intensity").toString().length() - 1);
                                if (intensityColor.contains("%")) {
                                    String[] colorArr = new String[3];
                                    int index = 0;
                                    for (String c : intensityColor.split(",")) {
                                        colorArr[index++] = c;
                                    }
                                    colorArr[2] = "90%";
                                    peptide.put("color", initColorMap(colorArr));
                                } else {
                                    int[] colorArr = new int[3];
                                    int index = 0;
                                    for (String c : intensityColor.split(",")) {
                                        colorArr[index++] = Integer.parseInt(c);
                                    }
                                    Color unselected = new Color(colorArr[0], colorArr[1], colorArr[2]).brighter();
                                    peptide.put("color", initColorMap(unselected));
                                }

                            }
                            return peptide;
                        }).forEachOrdered((peptide) -> {
                            entriesSet.add(peptide);
                        });
                    } catch (NumberFormatException exp) {
                        exp.printStackTrace();
                    }
                });

                if (peptideKey != null && !peptideKey.contains("null")) {
                    peptidesQueryMap.get(peptideKey).stream().map((peptide) -> {
                        int[] colorArr = new int[3];
                        String intensityColor = peptide.get("intensity").toString().substring(4, peptide.get("intensity").toString().length() - 1);
                        int index = 0;
                        for (String c : intensityColor.split(",")) {
                            colorArr[index++] = Integer.parseInt(c);
                        }
                        peptide.put("color", initColorMap(colorArr));
                        return peptide;
                    }).forEachOrdered((peptide) -> {
                        entriesSet.add(peptide);
                    });
                }

                break;
            }

        }
        String pdbAccession = (pdbMatchesSelect.getValue() + "").toLowerCase();
        liteMOL3DComponent.excuteQuery(pdbAccession, lastSelectedMatch.getEntity_id(chainId), chainId.toUpperCase(), initColorMap(basicColor), entriesSet);

    }

    public void selectPeptide(String peptideKey) {
        if (pdbMatchesSelect.isVisible()) {
            if (peptideKey != null) {
                selectPeptides(peptideKey);
            } else {
                selectPeptides(null);
            }
        }
    }

    private void contsructQueries(List<ChainBlock> selectedBlocks) {
        peptidesQueryMap.clear();
        this.proteinPeptidesMap.clear();
        this.proteinPeptides.forEach((peptide) -> {
            this.proteinPeptidesMap.put(peptide.getModifiedSequence(), peptide);
            this.peptidesQueryMap.put(peptide.getModifiedSequence(), new ArrayList<>());
            int start;
            Set<String> selectedChains = new LinkedHashSet<>();
            selectedBlocks.forEach((chain) -> {
                selectedChains.add(chain.getChain_id());
            });
            for (String chainId : selectedChains) {
                int current = 0;
                for (String seq : lastSelectedMatch.getSequence(chainId)) {
                    if (seq.toLowerCase().replace("i", "l").contains(peptide.getSequence().toLowerCase().replaceAll("i", "l"))) {
                        for (ChainBlock c : lastSelectedMatch.getChainBlocks(chainId)) {
                            start = seq.toLowerCase().replaceAll("i", "l").indexOf(peptide.getSequence().toLowerCase().replaceAll("i", "l"), current) + c.getStart_residue_number();
                            if (start == 0) {
                                continue;
                            }
                            int end = start + peptide.getSequence().length() - 1;
                            current = end;
                            ModificationMatch[] varMod = peptide.getVariableModifications();
                            peptide.setInvisibleOn3d(false);
                            peptidesQueryMap.get(peptide.getModifiedSequence()).add(initSequenceMap(chainId, lastSelectedMatch.getEntity_id(chainId), start, end, peptide.getValidation(), (varMod.length > 0), varMod, peptide.getSequence(), peptide.getPsmColor(), peptide.getIntensityColor()));
                            break;
                        }

                    } else {
                        peptide.setInvisibleOn3d(true);
                    }
                }
            }
        });

    }

    private HashMap<String, Object> initSequenceMap(String chainId, int entity_id, int start, int end, String validation, boolean modified, ModificationMatch[] modifications, String sequence, String psmNumberColor, String intensityColor) {
        HashMap<String, Object> sequenceMap = new HashMap<>();
        sequenceMap.put("entity_id", entity_id + "");
        sequenceMap.put("struct_asym_id", chainId.toUpperCase());
        sequenceMap.put("start_residue_number", start);
        sequenceMap.put("end_residue_number", end);
        sequenceMap.put("sequence_key", start + "_" + end);
        sequenceMap.put("validation", validation);
        sequenceMap.put("modified", modified);
        sequenceMap.put("modifications", modifications);
        sequenceMap.put("sequence", sequence);
        sequenceMap.put("psm", psmNumberColor);
        sequenceMap.put("intensity", intensityColor);
        return sequenceMap;

    }

    private HashMap<String, Integer> initColorMap(int[] color) {
        HashMap<String, Integer> colorMap = new HashMap<>();
        colorMap.put("r", color[0]);
        colorMap.put("g", color[1]);
        colorMap.put("b", color[2]);
        return colorMap;
    }

    private HashMap<String, String> initColorMap(String[] color) {
        HashMap<String, String> colorMap = new HashMap<>();
        colorMap.put("h", color[0]);
        colorMap.put("s", color[1]);
        colorMap.put("l", color[2]);
        return colorMap;
    }

    private HashMap<String, Integer> initColorMap(Color color) {
        HashMap<String, Integer> colorMap = new HashMap<>();
        colorMap.put("r", color.getRed());
        colorMap.put("g", color.getGreen());
        colorMap.put("b", color.getBlue());
        return colorMap;
    }

    public void setMode(int moleculeMode) {
        this.moleculeMode = moleculeMode;
        selectPeptide(lastSelectedPeptideKey);
    }

    public AbsoluteLayout getChainCoverageLayout() {
        return chainCoverageLayout;
    }

}
