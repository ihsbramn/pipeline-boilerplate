#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const handlebars = require("handlebars");
const commandLineArgs = require('command-line-args');
// const pullpush = require('./output/groovy/pullpush.js')

const optionDefinitions = [
    { name: 'build_directory', alias: 'd', type: String },
    // your build directory
    { name: 'type', alias: 't', type: String },
    // development - production - promotion - merge_request - release_candidate
    { name: 'framework', alias: 'f', type: String},
    // spring - vue
    { name: 'name', alias: 'n', type: String },
    // myallo/myhrweb 
    { name: 'repo_project', alias: 'r', type: String},
    // http://username:password@localhost:8484/root/testing3.git of project
    { name: 'branch', alias: 'b', type: String},
    // staging - main
    { name: 'ip_nexus_package', alias: 'p', type: String},
    // ip nexus for install package (NPM Pkg or etc)
    { name: 'ip_nexus_image', alias: 'i', type: String},
    // ip nexus for push image
    { name: 'repo_cicd', alias: 'c', type: String},
    // http://username:token@localhost:8484/root/testing3.git of cicd
    { name: 'output', alias: 'o', type: String}
    // pipeline - jenkinsfile - both
  ]

const options = commandLineArgs(optionDefinitions);
const templateGroovy = fs.readFileSync(path.join(__dirname, `./templates/${options.type}-${options.framework}.groovy`), 'utf8');
const toTemplateGroovy = handlebars.compile(templateGroovy);
const groovyFormat = toTemplateGroovy(options);

const templateJenkins = fs.readFileSync(path.join(__dirname, `./templates/jenkins-config.xml`), 'utf8');

const toTemplateJenkins = handlebars.compile(templateJenkins)
const toJenkinsFormat = toTemplateJenkins({content: groovyFormat});


// Output
if (options.output == 'pipeline') {
  // output as job @jenkins working directory
  if (!fs.existsSync(`${options.name.toLowerCase()}`)) fs.mkdirSync(`${options.name.toLowerCase()}`);
  fs.writeFileSync(`${options.name.toLowerCase()}/config.xml`, toJenkinsFormat);

  console.log('output as Pipeline Job at Jenkins App')
  return;
}

if( options.output == 'jenkinsfile'){
  // output as .groovy format @jenkins working directory
  if(!fs.existsSync(`/output/groovy`)) fs.mkdirSync(`/output/groovy`);
  fs.writeFileSync(`/output/groovy/${options.name.toLowerCase()}.groovy`, groovyFormat);

  console.log('output as Jenkins File')
  return;
}

if (options.output == 'both') {
  // output both job & .groovy @jenkins working directory
  if (!fs.existsSync(`${options.name.toLowerCase()}`)) fs.mkdirSync(`${options.name.toLowerCase()}`);
  fs.writeFileSync(`${options.name.toLowerCase()}/config.xml`, toJenkinsFormat);
  fs.writeFileSync(`/output/groovy/${options.name.toLowerCase()}.groovy`, groovyFormat);

  console.log('Both , Pipeline and Jenkinsfile')
  return;
}