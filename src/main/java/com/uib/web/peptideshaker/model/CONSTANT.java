package com.uib.web.peptideshaker.model;

import java.awt.Color;

/**
 * Main constant keywords
 *
 * @author Yehia Mokhtar Farag
 */
public class CONSTANT {

    public static final Color[] DEFAULT_CHARTS_COLOURS = new Color[]{new Color(219, 169, 1), new Color(110, 177, 206), new Color(213, 8, 8), new Color(4, 180, 95), new Color(174, 180, 4), new Color(10, 255, 14), new Color(244, 250, 88), new Color(255, 0, 64), new Color(246, 216, 206), new Color(189, 189, 189), new Color(255, 128, 0), Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK};
    public static final Color[] PROTEIN_VALIDATION_COLOURS = new Color[]{Color.DARK_GRAY, new Color(4, 180, 95), Color.ORANGE, new Color(213, 8, 8)};

    public static final Color[] PROTEIN_INFERENCE_COLOURS = new Color[]{Color.DARK_GRAY, new Color(4, 180, 95), new Color(245, 226, 80), Color.ORANGE, new Color(213, 8, 8)};

    public static final String GALAXY_SOURCE = "galaxy_source";
    public static final String USER_UPLOAD_SOURCE = "user_upload_source";
    public static final String PRIDE_SOURCE = "pride_source";

    public static final String WEB_PEPTEDSHAKER_FUNCTIONAL_HISTORY = "Web-PS-Functional-History";
    public static final String APP_MANAGMENT_BEAN = "AppManagmentBean";
    public static final String PUBLIC_USER_CAPTION = "Guest User <i>(public data)</i>";
    public static final String ID = "id";
    public static final String HISTORY_ID = "history_id";
    public static final String INPUTS = "inputs";
    public static final String USERNAME = "username";
    public static final String STORAGE = "nice_total_disk_usage";
    public static final String FILES_NUMBER = "#Files";
    public static final String TYPE = "type";
    public static final String DELETED = "deleted";
    public static final String VISIBLE = "visible";
    public static final String FILE_SIZE = "file_size";
    public static final String PURGED = "purged";
    public static final String STATE = "state";
    public static final String ERROR = "error";
    public static final String GALAXY_COLLECTION = "collection";
    public static final String GALAXY_FILE = "file";
    public static final String NAME = "name";
    public static final String JOB_SOURCE_ID = "job_source_id";
    public static final String URL = "url";
    public static final String SIZE = "size";
    public static final String CREATE_TIME = "create_time";
    public static final String EXTENSION = "extension";
    public static final String PEEK = "peek";
    public static final String GALAXY_FILE_OVERVIEW = "misc_info";
    public static final String COLLECTION_ELEMENTS = "elements";
    public static final String COLLECTION_OBJECT = "object";
    public static final String PS_DATASET_NUMBER = "#datasets";
    public static final String CREATING_JOB_ID = "creating_job";
    public static final String PSM_REPORT_FILE_NAME = "psm_report";
    public static final String TOOL_ID = "tool_id";
    public static final String TOOL_VERSION = "tool_version";
    public static final String JOB_INPUTS = "inputs";
    public static final String JOB_OUTPUTS = "outputs";
    public static final String QUANT_DATASET = "quant_ds";
    public static final String ID_DATASET = "id_ds";
    public static final String USER_UPLOADED_DATASET = "user_ploaded_dataset";
    public static final String SEARCH_GUI_FILE_EXTENSION = "searchgui_archive";
    public static final String ZIP_FILE_EXTENSION = "zip";
    public static final String TABULAR_FILE_EXTENSION = "tabular";
    public static final String CUI_FILE_EXTENSION = "cui";
    public static final String MGF_FILE_EXTENSION = "mgf";
    public static final String JSON_FILE_EXTENSION = "json";
    public static final String FASTA_FILE_EXTENSION = "fasta";
    public static final String PROTEIN_FILE_TYPE = "protein";
    public static final String PEPTIDE_FILE_TYPE = "peptide";
    public static final String THERMO_RAW_FILE_EXTENSION = "thermo.raw";
    public static final String mzML_FILE_EXTENSION = "mzml";
    public static final String TXT_FILE_EXTENSION = "txt";
    public static final String OK_STATUS = "ok";
    public static final String NEW_STATUS = "new";
    public static final String RUNNING_STATUS = "running";
    public static final String ERROR_STATUS = "error";

    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    public static final String UNICODE_FORMAT = "UTF8";
    public static final String OUTPUTS = "outputs";
    public static final String MOFF_TOOL_ID = "toolshed.g2.bx.psu.edu/repos/galaxyp/proteomics_moff/proteomics_moff/2.0.3.0";
    public static final String MOFF_TOOL_Version = "2.0.3.0";
    public static final String CONVERT_CHARACTERS_TOOL_ID = "Convert characters1";
    public static final String THERMO_RAW_CONVERTOR_TOOL_ID = "toolshed.g2.bx.psu.edu/repos/galaxyp/thermo_raw_file_converter/thermo_raw_file_converter/1.2.3+galaxy0";
    public static final String THERMO_RAW_CONVERTOR_TOOL_VERSION = "1.2.3+galaxy0";

    public static final String SEARCHGUI_TOOL_ID = "testtoolshed.g2.bx.psu.edu/repos/carlosh/peptideshaker_tests/search_gui/4.0.4.0";
    public static final String SEARCHGUI_TOOL_VERSION = "4.0.4.0";
    public static final String BUILD_LIST_TOOL_ID = "__BUILD_LIST__";
    public static final String BUILD_LIST_TOOL_VERSION = "1.0.0";
    public static final String PEPTIDESHAKER_TOOL_ID = "testtoolshed.g2.bx.psu.edu/repos/carlosh/peptideshaker_tests/peptide_shaker/2.0.5.0";
    public static final String PEPTIDESHAKER_TOOL_VERSION = "2.0.5.0";
    public static final String SERVER_TIMEZONE = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    public static final String[] EXTRA_COLOURS = new String[]{
        "C00000", "00C000", "0000C0", "C0C000", "C000C0", "00C0C0", "C0C0C0",
        "400000", "004000", "000040", "404000", "400040", "004040", "404040",
        "200000", "002000", "000020", "202000", "200020", "002020", "202020",
        "600000", "006000", "000060", "606000", "600060", "006060", "606060",
        "A00000", "00A000", "0000A0", "A0A000", "A000A0", "00A0A0", "A0A0A0",
        "E00000", "00E000", "0000E0", "E0E000", "E000E0", "00E0E0", "E0E0E0",};

    public static final String DATASET_SELECTION = "dataset_filter_selection";
    public static final String PROTEINS_SELECTION = "proteins_selection";
    public static final String PEPTIDES_SELECTION = "peptides_selection";

    public static final String VALIDATION_FILTER_ID = "validation_filter";
    public static final String PI_FILTER_ID = "pi_filter";
    public static final String CHROMOSOME_FILTER_ID = "chromosome_filter";
    public static final String MODIFICATIONS_FILTER_ID = "modifications_filter";
    public static final String INTENSITY_FILTER_ID = "intensityAllPep_filter";
    public static final String INTENSITY_UNIQUE_FILTER_ID = "intensityUniquePep_filter";
    public static final String PEPTIDES_NUMBER_FILTER_ID = "peptidesNum_filter";
    public static final String COVERAGE_FILTER_ID = "possibleCoverage_filter";
    public static final String PSM_NUMBER_FILTER_ID = "psmNum_filter";
    public static final String PROTEIN_TABLE_FILTER_ID = "proteins_table_filter";

    public static final String VALIDATION_CONFIDENT = "Confident";
    public static final String VALIDATION_DOUBTFUL = "Doubtful";
    public static final String VALIDATION_NOT_VALID = "Not Validated";
    public static final String NO_INFORMATION = "No Information";

    /**
     * Protein evidence options array.
     */
    public static final String[] PROTEIN_EVIDENCE = new String[]{NO_INFORMATION, "Protein", "Transcript", "Homology", "Predicted", "Uncertain"};

}
