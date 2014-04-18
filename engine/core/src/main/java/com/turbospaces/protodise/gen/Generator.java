package com.turbospaces.protodise.gen;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turbospaces.protodise.EnumDescriptor;
import com.turbospaces.protodise.MessageDescriptor;
import com.turbospaces.protodise.ProtoContainer;
import com.turbospaces.protodise.ProtoParserLexer;
import com.turbospaces.protodise.ProtoParserParser;
import com.turbospaces.protodise.ProtoParserParser.ProtoContext;
import com.turbospaces.protodise.ServiceDescriptor;

import freemarker.cache.ClassTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class Generator {
    private static Logger logger = LoggerFactory.getLogger( Generator.class );
    private final File outDir;
    private final String[] paths;
    private Template enumTemplate, classTemplate, protoTemplate, serviceTemplate;
    private final GenLanguage lang;
    private final String version = "0.1-SNAPSHOT";

    public Generator(String lang, File outDir, String... paths) throws IOException {
        this.lang = GenLanguage.valueOf( lang.toUpperCase() );
        this.outDir = outDir;
        this.paths = paths;

        Configuration cfg = new Configuration();
        cfg.setObjectWrapper( new BeansWrapper() );
        cfg.setDefaultEncoding( "UTF-8" );
        cfg.setTemplateLoader( new ClassTemplateLoader( getClass(), "/templates" ) );
        try {
            enumTemplate = cfg.getTemplate( lang + "/enum.ftl" );
            classTemplate = cfg.getTemplate( lang + "/class.ftl" );
            protoTemplate = cfg.getTemplate( lang + "/proto.ftl" );
            serviceTemplate = cfg.getTemplate( lang + "/service.ftl" );
        }
        catch ( IOException e ) {
            logger.error( e.getMessage(), e );
            throw e;
        }
    }

    public void run() throws Exception {
        Charset charset = Charset.forName( "UTF-8" );
        outDir.mkdirs();
        logger.info( "generating code into folder = {}", outDir );

        assert ( outDir.isDirectory() );
        ProtoGenerationContext ctx = new ProtoGenerationContext();
        //
        // parse straight protocol files for further stubs generation
        //
        for ( int i = 0; i < paths.length; i++ ) {
            String path = paths[i];
            File f = resource( path );

            String text = new String( Files.readAllBytes( f.toPath() ), charset );
            logger.info( "parsing protoc file = {}", path );
            ProtoContainer container = parse( text );

            String n = f.getName().substring( 0, f.getName().indexOf( ".protoc" ) );
            StringBuilder b = new StringBuilder();
            String[] parts = n.split( "[-_]" );
            for ( String s : parts ) {
                b.append( Character.toUpperCase( s.charAt( 0 ) ) + s.substring( 1 ) );
            }

            container.name = b.toString();
            ctx.containers.add( container );
        }
        //
        // parse imported, but skip generation
        //
        Set<String> allImports = new LinkedHashSet<String>();
        for ( ProtoContainer c : ctx.containers ) {
            allImports.addAll( c.imports );
        }

        for ( String path : allImports ) {
            String text = new String( Files.readAllBytes( resource( path ).toPath() ), charset );
            logger.info( "parsing imported protoc file = {}", path );
            ctx.imports.add( parse( text ) );
        }

        ctx.init( ctx );

        for ( ProtoContainer root : ctx.containers ) {
            Collection<EnumDescriptor> enums = root.enums.values();
            Collection<MessageDescriptor> messages = root.messages.values();
            Collection<ServiceDescriptor> services = root.services.values();
            File pkg = new File( outDir, root.getPkg().replace( '.', File.separatorChar ) );
            pkg.mkdirs();
            Map<String, Object> common = new HashMap<String, Object>();
            common.put( "pkg", root.getPkg() );
            common.put( "version", version );

            {
                StringWriter out = new StringWriter();
                Map<String, Object> model = new HashMap<String, Object>();
                model.put( "proto", root );
                model.putAll( common );
                protoTemplate.process( model, out );

                String filename = root.getName() + '.' + lang.name().toLowerCase();
                File f = new File( pkg, filename );
                f.getParentFile().mkdirs();
                Files.write( f.toPath(), out.toString().getBytes( charset ) );
            }

            for ( EnumDescriptor d : enums ) {
                StringWriter out = new StringWriter();
                Map<String, Object> model = new HashMap<String, Object>();
                model.put( "enum", d );
                model.putAll( common );
                enumTemplate.process( model, out );

                String filename = d.getName() + '.' + lang.name().toLowerCase();
                File f = new File( pkg, filename );
                f.getParentFile().mkdirs();
                Files.write( f.toPath(), out.toString().getBytes( charset ) );
            }

            for ( MessageDescriptor d : messages ) {
                StringWriter out = new StringWriter();
                Map<String, Object> model = new HashMap<String, Object>();
                model.put( "clazz", d );
                model.putAll( common );
                classTemplate.process( model, out );

                String filename = d.getName() + '.' + lang.name().toLowerCase();
                File f = new File( pkg, filename );
                f.getParentFile().mkdirs();
                Files.write( f.toPath(), out.toString().getBytes( charset ) );
            }

            for ( ServiceDescriptor s : services ) {
                StringWriter out = new StringWriter();
                Map<String, Object> model = new HashMap<String, Object>();
                model.put( "service", s );
                model.putAll( common );
                serviceTemplate.process( model, out );

                String filename = s.getName() + '.' + lang.name().toLowerCase();
                File f = new File( pkg, filename );
                f.getParentFile().mkdirs();
                Files.write( f.toPath(), out.toString().getBytes( charset ) );
            }
        }
    }
    public static ProtoContainer parse(String text) {
        ANTLRInputStream input = new ANTLRInputStream( text );
        ProtoParserLexer lexer = new ProtoParserLexer( input );
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        ProtoParserParser parser = new ProtoParserParser( tokens );
        parser.setTrace( logger.isDebugEnabled() );
        parser.removeErrorListeners();
        parser.addErrorListener( new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg,
                    RecognitionException e) {
                List<String> stack = ( (ProtoParserParser) recognizer ).getRuleInvocationStack();
                Collections.reverse( stack );
                logger.error( "rule stack: {}", stack );
                logger.error( "line {}:{} at {}: error={}", line, charPositionInLine, offendingSymbol, msg );
            }
        } );
        ProtoContext protoContext = parser.proto();
        ProtoContainer container = new ProtoContainer();
        Antlr4ProtoVisitor visitor = new Antlr4ProtoVisitor( parser, container );
        visitor.visit( protoContext );
        return container;
    }
    public static File resource(String path) {
        File f = new File( path );
        if ( f.exists() ) {
            return f;
        }
        else {
            URL resource = Thread.currentThread().getContextClassLoader().getResource( path );
            return new File( resource.getFile() );
        }
    }

    public static void main(String... args) throws Exception {
        File f = new File( args[0] );
        Generator g = new Generator( "java", f, Arrays.copyOfRange( args, 1, args.length ) );
        g.run();
    }
}
