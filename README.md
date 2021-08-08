# Features #

## Create/Edit songs ##
  * Create, edit songs in chordpro format (downloadable from most sites) i.e. plain text lyrics, within which chords, signified by square brackets, e.g. `[C#m7]`, are embedded - see example bellow:
<pre>
When I [E]find myself in [H]times of trouble<br>
[C#mi]Mother Mary [A]comes to me<br>
[E]speaking words of [H]wisdom let it [A]be [E]<br>
</pre>
  * Transpose all chords in the song up or down.

## Create song books ##
  * Export to PDF.
    * Ideal for creating a printable song book.
    * Formated for great readablity.
  * Export to HTML
    * Ideal for online presentation or convenient viewing in web-browser.
  * Export to `LaTex`
    * Allows professional postprocessing and customized printable output.
  * Export to EPub
    * Allows Viewing in E-Book readers, Tablets and Phones.

## Android Songbook Support ##
  * Browse and view songs
  * Transpose songs
  * Customize the UI and ordering
  * Download songbook directly form internet

## Usage: SongBook PC (song editing) ##
 * Download and install java 11
 * Download [songbook-pc.jar](https://gitlab.com/api/v4/projects/7109318/packages/generic/songbook-pc/latest/songbook-pc.jar?1)
 * Run

   
    java -jar songbook-pc.jar <path-to-song-directory>


## Usage: SongBook PWA (song sharing) ##
 * Using SongBook PC create your songbook
 * Export PDF and check that it looks good
 * Export JSON
 * Download [songbook-pwa-base.zip](https://gitlab.com/api/v4/projects/7109318/packages/generic/songbook-pwa-base/latest/songbook-pwa-base.zip?1)
 * Unzip the archive
 * Add the generated pdf and json to the root directory (preferably add hash at the end of the names)
 * Edit index.html and replace songbook-sample.json and songbook-sample.pdf with the names of your songbook files 
 * Put the directory to the internet
   
 * To do all of these steps automatically (optional)
    * Clone a repository with my songs [guitar-song-book-editor-songs](https://gitlab.com/janecekt/guitar-song-book-editor-songs)
    * Replace my songs with your songs
    * Push the changes into your own gitlab repository where you'll manage your songbook
    * Gitlab will automatically build a pdf and pwa-songbook for you and put them into packages
    * NOTE: Take a look at the .gitlab-ci.yaml and tweak it as necessary

---


# Downloads #
| **Filename** | **Description** |
|:-------------|:----------------|
| [songbook-pc.jar](https://gitlab.com/api/v4/projects/7109318/packages/generic/songbook-pc/latest/songbook-pc.jar?1) | Songbook viewer and editor for PC |
| [songbook.pdf](https://gitlab.com/api/v4/projects/7109320/packages/generic/songbook-pdf/latest/songbook.pdf?1) | Songbook in PDF format with my favourite songs (optimized for 2-sided printing) |
| [songbook-pwa.zip](https://gitlab.com/api/v4/projects/7109320/packages/generic/songbook-pwa/latest/songbook-pwa.zip?1) | Songbook in PDF format with my favourite songs (optimized for 2-sided printing) |
| [songbook-pwa-base.zip](https://gitlab.com/api/v4/projects/7109318/packages/generic/songbook-pwa-base/latest/songbook-pwa-base.zip?1) | Base songbook PWA phone/tablet app (contains no songs) |


# Licence #
```
Copyright (c) 2008 - Tomas Janecek.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
```
