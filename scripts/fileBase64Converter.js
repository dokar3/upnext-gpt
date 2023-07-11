#!/usr/bin/env node

const fs = require('fs');
const process = require("process")

const args = process.argv;

const OPT_FILE_TO_BASE64_TXT = "f2txt";
const OPT_BASE64_STRING_TO_FILE = "str2f";
const OPT_BASE64_TXT_FILE_TO_FILE = "txt2f";

const HELP = `Usage:
Convert a file to a base 64 encoded text file:
node fileBase64Converter.js ${OPT_FILE_TO_BASE64_TXT} input_file output.txt

Convert base 64 string to file:
node fileBase64Converter.js ${OPT_BASE64_STRING_TO_FILE} BASE_64_STRING output_file

Converter base 64 encoded text file to file:
node fileBase64Converter.js ${OPT_BASE64_TXT_FILE_TO_FILE} base64.txt output_file
`

function err(msg) {
  if (msg != null) {
    console.log(msg);
  }
  process.exit(1);
}

if (args.length < 2) {
  err();
  return;
}

if (args.length == 2 || args[2] === "--help" || args[2] === "-h") {
  err(HELP);
  return;
}

const opt = args[2];
if (opt != OPT_FILE_TO_BASE64_TXT &&
  opt != OPT_BASE64_STRING_TO_FILE &&
  opt != OPT_BASE64_TXT_FILE_TO_FILE) {
  err(`Unknown option ${opt}, use --help or -h to see available options`)
  return;
}

if (args.length == 3) {
  err("No input (file).");
  return;
}
const input = args[3];
if (opt === OPT_FILE_TO_BASE64_TXT || opt === OPT_BASE64_TXT_FILE_TO_FILE) {
  if (!fs.existsSync(input)) {
    err(`Input file does not exists: ${input}`);
    return;
  }
}

if (args.length == 4) {
  err("Output file is not specified");
  return;
}
const output = args[4];

if (opt === OPT_FILE_TO_BASE64_TXT) {
  const file_buffer = fs.readFileSync(input);
  const base64 = file_buffer.toString('base64');
  fs.writeFileSync(output, base64)
} else if (opt === OPT_BASE64_STRING_TO_FILE) {
  fs.writeFileSync(output, input, { encoding: 'base64' });
} else if (opt === OPT_BASE64_TXT_FILE_TO_FILE) {
  const file_buffer = fs.readFileSync(input);
  const base64 = file_buffer.toString();
  fs.writeFileSync(output, base64, { encoding: 'base64' });
}
