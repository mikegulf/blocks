#!/usr/bin/env python

#Script to parse all blocks level files in a generated blocks directory
#Simply execute it in the top directory containing all the directories to analyze
#Or pass the directory as the first command-line argument

#Generates a 'firstContact.csv' file in each directory

import os, sys, re

def find_first_timestamps(fname):

        with open(fname) as f:
                header = f.readline().split(',') #box0x, box0y, box0moved, ... boxNmoved, timestamp, level, condition
                boxes = (len(header) / 3) - 1
                stamps = [None] * boxes
                
                for line in (l.split(',') for l in f.readlines()):
                        for i in xrange(len(stamps)):
                                if stamps[i] == None and line[(3*i)+2] == '1':
                                        stamps[i] = line[-3]

                return stamps
        

root = sys.argv[1] if len(sys.argv) > 1 else os.getcwd()

fileRegex = re.compile(r'blocks(\d+[a-z]?)(\w+?).csv')

for baseDir in [name for name in os.listdir(root) if os.path.isdir(os.path.join(root, name))]:

    print 'Parsing levels in directory: ' + baseDir
    
    out_rows = ['level,condition,timestamps\n']

    for fname in os.listdir(baseDir):
        
        match = fileRegex.match(fname)
        if match:
            
            level = match.group(1)
            condition = match.group(2)
            
            try:
               stamps = find_first_timestamps(os.path.join(baseDir, fname))

               out_rows.append(level + ',' + condition + ',' + ','.join(map(str, stamps)) +'\n')
               
            except IOError as e:
                print >>sys.stderr, 'Error opening "%s"' % fname

    if len(out_rows) > 1:
        with open(baseDir + r'\firstContact.csv', 'wc') as f:
            f.writelines(out_rows)
                
    else:
        print >>sys.stderr, 'No level files found in "%s"' % baseDir
