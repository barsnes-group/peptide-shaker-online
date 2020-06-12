/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.pscomponents;

import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.io.biology.protein.SequenceProvider;
import com.compomics.util.experiment.mass_spectrometry.spectra.Spectrum;
import com.compomics.util.parameters.identification.IdentificationParameters;

/**
 *this class contain all the required information to load spectrum data
 * @author Yehia Farag
 */
public class SpectrumInformation {
    private   Spectrum spectrum;
    private String charge;
    private double fragmentIonAccuracy;
    private IdentificationParameters identificationParameters;
    private SpectrumMatch spectrumMatch;
    private Object spectrumId;
    private int maxCharge;
    private double mzError;
    private SequenceProvider  sequenceProvider;

    public int getMaxCharge() {
        return maxCharge;
    }

    public void setMaxCharge(int maxCharge) {
        this.maxCharge = maxCharge;
    }

    public Object getSpectrumId() {
        return spectrumId;
    }

    public void setSpectrumId(Object spectrumId) {
        this.spectrumId = spectrumId;
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public double getFragmentIonAccuracy() {
        return fragmentIonAccuracy;
    }

    public void setFragmentIonAccuracy(double fragmentIonAccuracy) {
        this.fragmentIonAccuracy = fragmentIonAccuracy;
    }

    public IdentificationParameters getIdentificationParameters() {
        return identificationParameters;
    }

    public void setIdentificationParameters(IdentificationParameters identificationParameters) {
        this.identificationParameters = identificationParameters;
    }

    public SpectrumMatch getSpectrumMatch() {
        return spectrumMatch;
    }

    public void setSpectrumMatch(SpectrumMatch spectrumMatch) {
        this.spectrumMatch = spectrumMatch;
    }

    public double getMzError() {
        return mzError;
    }

    public void setMzError(double mzError) {
        this.mzError = mzError;
    }

    public SequenceProvider getSequenceProvider() {
        return sequenceProvider;
    }

    public void setSequenceProvider(SequenceProvider sequenceProvider) {
        this.sequenceProvider = sequenceProvider;
    }
    
}
