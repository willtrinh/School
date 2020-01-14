from sys import argv

import scheduler

input_file = argv[1]
output_file = argv[2]

# Read text file
with open(input_file) as f:
	lines = f.readlines()

ints_list = []
for line in lines:
	i_list = list(map(int, line.split(' ')))
	ints_list.append(i_list)

runs = []
for ints in ints_list:
	ps = scheduler.purse(ints)
	runs.append(ps)

scheduling_results = []
for run in runs:
	scheduling_results.append(scheduler.schedule(run))

out_f = open(output_file, 'w')
for turnarounds in scheduling_results:
	scheduler.report(out_f, turnarounds)
	out_f.write('\n')
out_f.close()