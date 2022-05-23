package de.fluffeliger.wyldlife.modules;

import de.fluffeliger.wyldlife.WyldLife;

public abstract class Module implements IModule{

    public WyldLife instance = WyldLife.getInstance();
    public boolean enabled = false;
}
