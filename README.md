# PeptideShaker Online
* [Introduction](#introduction)
* [Read Me](#read-me)
* [Setup](#setup)

----
## Introduction
Peptideshaker Online is a user-friendly web-based framework for processing mass spectrometry-based proteomics data, from raw file conversion to interactive visualization of the results. The framework consists of two main components; a [Galaxy-based backend](https://github.com/barsnes-group/peptide-shaker-online/wiki/PeptideShaker-Online:-Backend) where the search and analysis processing is performed and the data storage is located and  a [web-based front-end](https://github.com/barsnes-group/peptide-shaker-online/wiki) supporting interactive visualizations of PeptideShaker projects.
The data processing is done via the Galaxy platform using: (i) [ThermoRawFileParser]() for converting Thermo raw files into mzML or mgf; (ii) [SearchGUI]() for protein identification based on ten proteomics search and de novo engines, namely [OMSSA](), [X! Tandem](), [MyriMatch](), [MS Amanda](), [MS-GF+](), [Comet](), [Tide](), [MetaMorpheus]() , [DirectTag]]()and [Novor](); (iii) PeptideShaker for interpretation of the protein identification data from SearchGUI; and finally (iv) [moFF]() for extracting MS1 intensities from the spectrum files. 
Spectrum input is supported as either mgf and mzML for identification or Thermo raw files for both identification and quantification.


