<#-- @ftlvariable name="bookId" type="java.lang.String" -->
<#-- @ftlvariable name="bookTitle" type="java.lang.String" -->
<#-- @ftlvariable name="entryList" type="java.util.Collection<com.songbook.pc.util.EPubBuilder.Entry>" -->
<?xml version="1.0" encoding="UTF-8"?>
<ncx xmlns="http://www.daisy.org/z3986/2005/ncx/" version="2005-1">
  <head>
    <meta name="dtb:uid" content="${bookId}"/>
    <meta name="dtb:depth" content="1"/>
    <meta name="dtb:totalPageCount" content="0"/>
    <meta name="dtb:maxPageNumber" content="0"/>
  </head>
  <docTitle>
    <text>${bookTitle}</text>
  </docTitle>
  <navMap>
      <#list entryList as entry>
          <#if entry.userReadable == true>
              <navPoint id="${entry.id}" playOrder="${entry_index + 1}">
                  <navLabel>
                      <text>${entry.name}</text>
                  </navLabel>
                  <content src="${entry.fileName}"></content>
              </navPoint>
          </#if>
    </#list>
  </navMap>
</ncx>