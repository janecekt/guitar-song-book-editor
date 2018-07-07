import * as _ from "lodash";

const chordToIdx = new Map<string,number>([
    ['C',  0],
    ['C#', 1],
    ['D',  2],
    ['D#', 3],
    ['Es', 3],
    ['E',  4],
    ['F',  5],
    ['F#', 6],
    ['G',  7],
    ['G#', 8],
    ['As', 8],
    ['A',  9],
    ['A#', 10],
    ['B',  10],
    ['H',  11],
]);

const idxToChord = new Map<string,string>([
    ['0', 'C'],
    ['1', 'C#'],
    ['2', 'D'],
    ['3', 'Es'],
    ['4',  'E'],
    ['5',  'F'],
    ['6', 'F#'],
    ['7', 'G'],
    ['8', 'G#'],
    ['9', 'A'],
    ['10', 'B'],
    ['11', 'H']
]);

export function transposeChord(chord : string, transposeBy : number) : string {
    if (transposeBy === 0 || _.isNil(chord)) {
        return chord;
    }
    const base = (chord.length > 1)
        ? ((chord.charAt(1) == 's') || (chord.charAt(1) == '#')) ? chord.substring(0, 2) : chord.substring(0, 1)
        : chord;

    const suffix = chord.replace(base, "");

    // Transpose base
    const chordIdx = chordToIdx.get(base);
    if (_.isNil(chordIdx)) {
        console.error('Unknown chord',  base, 'from', chord);
        return '';
    }
    const transposedChordIdx = (chordIdx + 12 + (transposeBy % 12)) % 12;
    return idxToChord.get('' + transposedChordIdx) + suffix;
}