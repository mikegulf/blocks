#!/usr/bin/env python

#Script to parse all blocks level files in a generated blocks directory
#Simply execute it in the top directory containing all the directories to analyze
#Or pass the directory as the first command-line argument

#Generates a 'firstContact.csv' file in each directory

import os, sys, re, zipfile

def find_first_timestamps(f):

        header = f.readline().split(',') #box0x, box0y, box0moved, ... boxNmoved, timestamp, level, condition
        boxes = (len(header) / 3) - 1
        stamps = [None] * boxes
        
        for line in (map(str.strip, l.split(',')) for l in f.readlines()):
                for i in xrange(len(stamps)):
                        level = line[-2]
                        condition = line[-1]
                        if stamps[i] == None and line[(3*i)+2] == '1':
                                stamps[i] = line[-3]

        return level, condition, stamps
        

root = sys.argv[1] if len(sys.argv) > 1 else os.getcwd()

out_name = 'firstTimestamps.csv'

fileRegex = re.compile(r'blocks\d+[a-z]?\w+?.csv')

for name, baseZip in ((name, zipfile.ZipFile(name, 'a')) for name in os.listdir(root) if zipfile.is_zipfile(name)):

    print 'Parsing levels in file: ' + name
    
    out_rows = 'level,condition,timestamps\n'

    tmpZip = zipfile.ZipFile(name + '_tmp', 'w') if out_name in baseZip.namelist() else None


    for fname, f in ((fname, baseZip.open(fname)) for fname in baseZip.namelist() if fileRegex.match(fname)):
        
        level, condition, stamps = find_first_timestamps(f)

        out_rows += level + ',' + condition + ',' + ','.join(map(str, stamps)) +'\n'

        if tmpZip:
                tmpZip.writestr(fname, f.read())

        f.close()
               
    if len(out_rows.split('\n')) > 1:
        outZip = tmpZip if tmpZip else baseZip
        outZip.writestr(out_name,out_rows)
                
    else:
        print >>sys.stderr, 'No level files found in "%s"' % name

    if tmpZip:

        try:
                tmpZip.writestr('mazeResponses.csv', baseZip.read('mazeResponses.csv'))
        except KeyError:
                pass

        tmpZip.close()
        baseZip.close()
        os.remove(name)
        os.rename(name + '_tmp', name)

    baseZip.close()


print 'Discovered timestamps for all zip files in ' + root
