from sys import argv
import Scheduler


inputs = argv[1]
outputs = argv[2]

# Read file
with open(inputs) as f:
	lines = f.readlines()

in_list = []

for l in lines:
	i_list = list(map(int, l.split(' ')))
	in_list.append(i_list)

process_list = []

for inputs in in_list:
	p = Scheduler.read_file(inputs)
	process_list.append(p)

result_list = []

for process in process_list:
	result_list.append(Scheduler.scheduling(process))

with open(outputs, 'w') as f:
	for turnaround_t in result_list:
		Scheduler.write_file(f, turnaround_t)
		f.write('\n')

