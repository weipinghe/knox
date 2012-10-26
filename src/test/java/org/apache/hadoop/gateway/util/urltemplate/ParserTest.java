/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.gateway.util.urltemplate;

import org.junit.Ignore;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ParserTest {

  private void assertBasics(
      Template template,
      boolean isAbsolute,
      boolean isDirectory,
      boolean hasQuery,
      int pathSegmentsSize,
      int querySegmentsSize ) {
    assertThat( "Incorrect isAbsolute value.", template.isAbsolute(), equalTo( isAbsolute ) );
    assertThat( "Incorrect isDirectory value.", template.isDirectory(), equalTo( isDirectory ) );
    assertThat( "Incorrect hasQuery value.", template.hasQuery(), equalTo( hasQuery ) );
    assertThat( "Incorrect path size.", template.getPath().size(), equalTo( pathSegmentsSize ) );
    assertThat( "Incorrect query size.", template.getQuery().size(), equalTo( querySegmentsSize ) );
  }

  public void assertPath(
      Template template,
      int index,
      String paramName,
      String valuePattern ) {
    Path segment = template.getPath().get( index );
    assertThat( "Incorrect template param name.", segment.getParamName(), equalTo( paramName ) );
    assertThat( "Incorrect template value pattern.", segment.getValuePattern(), equalTo( valuePattern ) );
  }

  public void assertPath(
      Template template,
      int index,
      String paramName,
      String valuePattern,
      int type,
      int minRequired,
      int maxAllowed ) {
    Path segment = template.getPath().get( index );
    assertThat( "Param name wrong.", segment.getParamName(), equalTo( paramName ) );
    assertThat( "Value pattern wrong.", segment.getValuePattern(), equalTo( valuePattern ) );
    assertThat( "Segment type wrong.", segment.getType(), equalTo( type ) );
    assertThat( "Segment min required wrong.", segment.getMinRequired(), equalTo( minRequired ) );
    assertThat( "Segment max allowed wrong.", segment.getMaxAllowed(), equalTo( maxAllowed ) );
  }

  public void assertQuery(
      Template template,
      String queryName,
      String paramName,
      String valuePattern ) {
    Query segment = template.getQuery().get( queryName );
    assertThat( "Query name wrong.", segment.getQueryName(), equalTo( queryName ));
    assertThat( "Param name wrong.", segment.getParamName(), equalTo( paramName ));
    assertThat( "value pattern wrong.", segment.getValuePattern(), equalTo( valuePattern ));
  }

  public void assertQuery(
      Template template,
      String queryName,
      String paramName,
      String valuePattern,
      int type,
      int minRequired,
      int maxAllowed ) {
    Query segment = template.getQuery().get( queryName );
    assertThat( "Query name wrong.", segment.getQueryName(), equalTo( queryName ));
    assertThat( "Param name wrong.", segment.getParamName(), equalTo( paramName ));
    assertThat( "value pattern wrong.", segment.getValuePattern(), equalTo( valuePattern ));
    assertThat( "Segment type wrong.", segment.getType(), equalTo( type ) );
    assertThat( "Segment min required wrong.", segment.getMinRequired(), equalTo( minRequired ) );
    assertThat( "Segment max allowed wrong.", segment.getMaxAllowed(), equalTo( maxAllowed ) );
  }

  @Test
  public void testCompleteUrl() throws URISyntaxException {
    String text;
    Template template;

    text = "foo://username:password@example.com:8042/over/there/index.dtb?type=animal&name=narwhal#nose";
    template = Parser.parse( text );
    assertBasics( template, true, false, true, 3, 2 );
  }

  @Ignore( "TODO" )
  @Test
  public void testInvalidPatterns() {
    //TODO: ? in wrong spot.
    //TODO: & in wrong spots.
  }

  @Ignore( "TODO" )
  @Test
  public void testRegexPatterns() {
  }

  @Test
  public void testTemplates() throws URISyntaxException {
    String text;
    Template template;

    text = "{path}";
    template = Parser.parse( text );
    assertBasics( template, false, false, false, 1, 0 );
    assertPath( template, 0, "path", "*" );

    text = "{pathA}/{pathB}";
    template = Parser.parse( text );
    assertBasics( template, false, false, false, 2, 0 );
    assertPath( template, 0, "pathA", "*" );
    assertPath( template, 1, "pathB", "*" );

    text = "?paramA={valueA}";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 0, 1 );
    assertQuery( template, "paramA", "valueA", "*" );

    text = "?paramA={valueA}&paramB={valueB}";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 0, 2 );
    assertQuery( template, "paramA", "valueA", "*" );
    assertQuery( template, "paramB", "valueB", "*" );

    text = "{pathA}?paramA={valueA}";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 1, 1 );
    assertPath( template, 0, "pathA", "*" );
    assertQuery( template, "paramA", "valueA", "*" );
  }

  @Test
  public void testStaticPatterns() throws Exception {
    String text;
    Template template;

    text = "";
    template = Parser.parse( text );
    assertBasics( template, false, false, false, 0, 0 );

    text = "/";
    template = Parser.parse( text );
    assertBasics( template, true, true, false, 0, 0 );

    text = "?";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 0, 0 );

    text = "path";
    template = Parser.parse( text );
    assertBasics( template, false, false, false, 1, 0 );
    assertPath( template, 0, "", "path" );

    text = "/path";
    template = Parser.parse( text );
    assertBasics( template, true, false, false, 1, 0 );
    assertPath( template, 0, "", "path" );

//    text = "//path";
//    template = Parser.parse( text );
//    assertBasics( template, true, false, false, 1, 0 );
//    assertPath( template, 0, "", "path" );

    text = "path/";
    template = Parser.parse( text );
    assertBasics( template, false, true, false, 1, 0 );
    assertPath( template, 0, "", "path" );

    text = "path//";
    template = Parser.parse( text );
    assertBasics( template, false, true, false, 1, 0 );
    assertPath( template, 0, "", "path" );

    text = "/path/";
    template = Parser.parse( text );
    assertBasics( template, true, true, false, 1, 0 );
    assertPath( template, 0, "", "path" );

//    text = "//path//";
//    template = Parser.parse( text );
//    assertBasics( template, true, true, false, 1, 0 );
//    assertPath( template, 0, "", "path" );

    text = "pathA/pathB";
    template = Parser.parse( text );
    assertBasics( template, false, false, false, 2, 0 );
    assertPath( template, 0, "", "pathA" );
    assertPath( template, 1, "", "pathB" );

    text = "pathA//pathB";
    template = Parser.parse( text );
    assertBasics( template, false, false, false, 2, 0 );
    assertPath( template, 0, "", "pathA" );
    assertPath( template, 1, "", "pathB" );

    text = "/pathA/pathB";
    template = Parser.parse( text );
    assertBasics( template, true, false, false, 2, 0 );
    assertPath( template, 0, "", "pathA" );
    assertPath( template, 1, "", "pathB" );

    text = "/pathA//pathB";
    template = Parser.parse( text );
    assertBasics( template, true, false, false, 2, 0 );
    assertPath( template, 0, "", "pathA" );
    assertPath( template, 1, "", "pathB" );

    text = "pathA/pathB/";
    template = Parser.parse( text );
    assertBasics( template, false, true, false, 2, 0 );
    assertPath( template, 0, "", "pathA" );
    assertPath( template, 1, "", "pathB" );

    text = "pathA//pathB/";
    template = Parser.parse( text );
    assertBasics( template, false, true, false, 2, 0 );
    assertPath( template, 0, "", "pathA" );
    assertPath( template, 1, "", "pathB" );

    text = "/pathA/pathB/";
    template = Parser.parse( text );
    assertBasics( template, true, true, false, 2, 0 );
    assertPath( template, 0, "", "pathA" );
    assertPath( template, 1, "", "pathB" );

    text = "/pathA//pathB/";
    template = Parser.parse( text );
    assertBasics( template, true, true, false, 2, 0 );
    assertPath( template, 0, "", "pathA" );
    assertPath( template, 1, "", "pathB" );

    text = "/?";
    template = Parser.parse( text );
    assertBasics( template, true, true, true, 0, 0 );

//    text = "//??";
//    template = Parser.parse( text );
//    assertBasics( template, true, true, true, 0, 0 );

    text = "?name=value";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 0, 1 );
    assertQuery( template, "name", "", "value" );

    text = "?name1=value1&name2=value2";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 0, 2 );
    assertQuery( template, "name1", "", "value1" );
    assertQuery( template, "name2", "", "value2" );

    text = "?name1=value1&&name2=value2";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 0, 2 );
    assertQuery( template, "name1", "", "value1" );
    assertQuery( template, "name2", "", "value2" );

    text = "/?name=value";
    template = Parser.parse( text );
    assertBasics( template, true, true, true, 0, 1 );
    assertQuery( template, "name", "", "value" );

    text = "/?name1=value1&name2=value2";
    template = Parser.parse( text );
    assertBasics( template, true, true, true, 0, 2 );
    assertQuery( template, "name1", "", "value1" );
    assertQuery( template, "name2", "", "value2" );
  }

  @Test
  public void testParameterizedPathTemplatesWithWildcardAndRegex() throws URISyntaxException {
    String text;
    Template template;

    text = "{path}";
    template = Parser.parse( text );
    assertBasics( template, false, false, false, 1, 0 );
    assertPath( template, 0, "path", "*", Segment.WILDCARD, 1, 1 );

    text = "{path=static}";
    template = Parser.parse( text );
    assertBasics( template, false, false, false, 1, 0 );
    assertPath( template, 0, "path", "static", Segment.STATIC, 1, 1 );

    text = "{path=*}";
    template = Parser.parse( text );
    assertBasics( template, false, false, false, 1, 0 );
    assertPath( template, 0, "path", "*", Segment.WILDCARD, 1, 1 );

    text = "{path=**}";
    template = Parser.parse( text );
    assertBasics( template, false, false, false, 1, 0 );
    assertPath( template, 0, "path", "**", Segment.WILDCARD, 0, Integer.MAX_VALUE );

    text = "{path=wild*card}";
    template = Parser.parse( text );
    assertBasics( template, false, false, false, 1, 0 );
    assertPath( template, 0, "path", "wild*card", Segment.REGEX, 1, 1 );
  }

  @Test
  public void testParameterizedQueryTemplatesWithWildcardAndRegex() throws URISyntaxException {
    String text;
    Template template;

    text = "?query={param}";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 0, 1 );
    assertQuery( template, "query", "param", "*", Segment.WILDCARD, 1, 1 );

    text = "?query={param=static}";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 0, 1 );
    assertQuery( template, "query", "param", "static", Segment.STATIC, 1, 1 );

    text = "?query={param=*}";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 0, 1 );
    assertQuery( template, "query", "param", "*", Segment.WILDCARD, 1, 1 );

    text = "?query={param=**}";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 0, 1 );
    assertQuery( template, "query", "param", "**", Segment.WILDCARD, 0, Integer.MAX_VALUE );

    text = "?query={param=wild*card}";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 0, 1 );
    assertQuery( template, "query", "param", "wild*card", Segment.REGEX, 1, 1 );
  }

  @Test
  public void testGlobPattern() throws URISyntaxException {
    String text;
    Template template;

    text = "**";
    template = Parser.parse( text );
    assertBasics( template, false, false, false, 1, 0 );

    text = "/**";
    template = Parser.parse( text );
    assertBasics( template, true, false, false, 1, 0 );

    text = "**/";
    template = Parser.parse( text );
    assertBasics( template, false, true, false, 1, 0 );

    text = "/**/";
    template = Parser.parse( text );
    assertBasics( template, true, true, false, 1, 0 );

    text = "/**/path";
    template = Parser.parse( text );
    assertBasics( template, true, false, false, 2, 0 );
  }

  @Ignore( "TODO" )
  @Test
  public void testPatternsWithSchemeAndAuthority() throws URISyntaxException {
    String text;
    Template template;

    text = "http:";
    template = Parser.parse( text );

    text = "http:/path";
    template = Parser.parse( text );

    text = "http://host";
    template = Parser.parse( text );

    text = "http://host/";
    template = Parser.parse( text );

    text = "http://host:80";
    template = Parser.parse( text );

    text = "http://host:80/";
    template = Parser.parse( text );


    text = "{scheme}:";
    template = Parser.parse( text );

    text = "{scheme}:/{path}";
    template = Parser.parse( text );

    text = "{scheme}://{host}";
    template = Parser.parse( text );

    text = "{scheme}://{host}/";
    template = Parser.parse( text );

    text = "{scheme}://{host}:{port}";
    template = Parser.parse( text );

    text = "{scheme}://{host}:{port}/";
    template = Parser.parse( text );


    text = "{scheme=http}:/{path=index.html}";
    template = Parser.parse( text );

    text = "{scheme=http}://{host=*.com}";
    template = Parser.parse( text );

    text = "{scheme=https}://{host=*.edu}/";
    template = Parser.parse( text );

    text = "{scheme=rmi}://{host=*}:{port=80}";
    template = Parser.parse( text );

    text = "{scheme=ftp}://{host=localhost*}:{port=*80}/";
    template = Parser.parse( text );
  }

  @Test
  public void testEdgeCases() throws URISyntaxException {
    String text;
    Template template;

    text = "//";
    template = Parser.parse( text );
    assertBasics( template, false, false, false, 0, 0 );
    assertThat( template.hasAuthority(), equalTo( true ) );

    text = "??";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 0, 0 );

    text = "??name=value";
    template = Parser.parse( text );
    assertBasics( template, false, false, true, 0, 1 );
    assertQuery( template, "name", "", "value" );

  }

}