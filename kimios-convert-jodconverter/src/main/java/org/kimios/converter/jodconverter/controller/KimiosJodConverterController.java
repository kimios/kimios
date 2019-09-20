package org.kimios.converter.jodconverter.controller;

import org.kimios.kernel.controller.AKimiosController;

public class KimiosJodConverterController extends AKimiosController implements IKimiosJodConverterController {
    @Override
    public void fakeMethod(String str) {
        System.out.println("Hello " + str);
    }
}
