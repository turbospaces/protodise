package com.turbospaces.protodise.gen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
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
    private static Logger LOGGER = LoggerFactory.getLogger( Generator.class );

    private final File outDir;
    private final String[] paths;
    private Template enumTemplate, classTemplate, protoTemplate, serviceTemplate;
    private final GenLanguage lang;
    private final String version = "0.1-SNAPSHOT";

    public Generator(String... args) throws Exception {
        LOGGER.info( "generating with options={}", Arrays.toString( args ) );
        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new GnuParser();
        Options options = new Options();

        options.addOption( "l", "lang", true, "generation languaga(java, cs, flash, etc)" );
        options.addOption( "o", "output", true, "output directory" );
        options.addOption( "f", "files", true, "list of files" );

        try {
            CommandLine cmd = parser.parse( options, args );
            this.lang = GenLanguage.valueOf( cmd.getOptionValue( "l" ).toString().trim().toUpperCase() );
            this.outDir = new File( cmd.getOptionValue( "o" ).toString().trim() );
            this.paths = cmd.getOptionValues( "f" );
        }
        catch ( Exception e ) {
            formatter.printHelp( "protocol gen options", options );
            throw e;
        }

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
            LOGGER.error( e.getMessage(), e );
            throw e;
        }
    }
    public void run() throws Exception {
        outDir.mkdirs();
        LOGGER.info( "generating code into folder = {}", outDir );

        assert ( outDir.isDirectory() );
        ProtoGenerationContext ctx = new ProtoGenerationContext();
        //
        // parse straight protocol files for further stubs generation
        //
        for ( int i = 0; i < paths.length; i++ ) {
            String path = paths[i];
            File f = resource( path );

            String text = readFile( f );
            LOGGER.info( "parsing protoc file = {}", path );
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
            String text = readFile( resource( path ) );
            LOGGER.info( "parsing imported protoc file = {}", path );
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
                writeFile( f, out.toString() );
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
                writeFile( f, out.toString() );
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
                writeFile( f, out.toString() );
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
                writeFile( f, out.toString() );
            }
        }
    }
    public static ProtoContainer parse(String text) {
        ANTLRInputStream input = new ANTLRInputStream( text );
        ProtoParserLexer lexer = new ProtoParserLexer( input );
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        ProtoParserParser parser = new ProtoParserParser( tokens );
        parser.setTrace( LOGGER.isDebugEnabled() );
        parser.removeErrorListeners();
        parser.addErrorListener( new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg,
                                    RecognitionException e) {
                List<String> stack = ( (ProtoParserParser) recognizer ).getRuleInvocationStack();
                Collections.reverse( stack );
                LOGGER.error( "rule stack: {}", stack );
                LOGGER.error( "line {}:{} at {}: error={}", line, charPositionInLine, offendingSymbol, msg );
            }
        } );
        ProtoContext protoContext = parser.proto();
        ProtoContainer container = new ProtoContainer();
        Antlr4ProtoVisitor visitor = new Antlr4ProtoVisitor( parser, container );
        visitor.visit( protoContext );
        return container;
    }
    public static File resource(String path) throws FileNotFoundException {
        File f = new File( path );
        if ( f.exists() ) {
            return f;
        }
        else {
            URL resource = Thread.currentThread().getContextClassLoader().getResource( path );
            f = new File( resource.getFile() );
            if ( !f.exists() ) {
                throw new FileNotFoundException( path );
            }
            return f;
        }
    }

    public static String readFile(File f) throws IOException {
        FileInputStream fstream = new FileInputStream( f );
        InputStreamReader in = new InputStreamReader( fstream );
        BufferedReader br = new BufferedReader( in );

        StringBuffer text = new StringBuffer();
        try {
            for ( String line; ( line = br.readLine() ) != null; ) {
                text.append( line );
            }
        }
        finally {
            if ( fstream != null ) {
                fstream.close();
            }
            if ( in != null ) {
                in.close();
            }
        }
        return text.toString();
    }
    public static void writeFile(File f, String text) throws FileNotFoundException {
        f.getParentFile().mkdirs();
        PrintWriter pw = new PrintWriter( f );
        pw.println( text );
        pw.flush();
        pw.close();
    }
    public static void main(String... args) throws Exception {
        Generator g = new Generator( args );
        g.run();
    }
}
