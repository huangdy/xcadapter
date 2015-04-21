package com.leidos.xchangecore.adapter;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.file.Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leidos.xchangecore.adapter.ui.UploadPage;

public class XchangeCoreAdapter
extends WebApplication {

    private Folder uploadFolder = null;
    private final Logger logger = LoggerFactory.getLogger(XchangeCoreAdapter.class);

    /**
     * Constructor.
     */
    public XchangeCoreAdapter() {

    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends Page> getHomePage() {

        return UploadPage.class;
    }

    /**
     * @return the folder for uploads
     */
    public Folder getUploadFolder() {

        return this.uploadFolder;
    }

    @Override
    protected void init() {

        super.init();

        /*
        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("com.leidos.xchangecore.adapter.config.AdapterSpringConfig");
        ctx.refresh();
        getComponentInitializationListeners().add((IComponentInitializationListener) new SpringComponentInjector(this,
                                                                                                                 ctx));
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
         */

        this.getResourceSettings().setThrowExceptionOnMissingResource(false);

        this.uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "xchangecore-uploads");
        // this.uploadFolder.mkdirs();

        // need to enable explicitly
        this.getApplicationSettings().setUploadProgressUpdatesEnabled(true);
    }
}
