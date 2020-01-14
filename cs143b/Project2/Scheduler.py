import Process
import copy


class Scheduler:
    def __init__(self, process_list):
        # current running process
        self.running_process = None
        # remaining processes
        self.remaining = process_list
        # waiting processes queue
        self.waiting_queue = []
        # finished processes
        self.finished = []
        # time elapsed
        self.current_time = 0

    def is_started(self):
        if self.running_process:
            self.running_process.running()
        for process in self.waiting_queue:
            process.waiting()
        self.current_time = self.current_time + 1

    # sort remaining processes arrival time by current time and append to waiting queue
    def is_sorted(self):
        idx = 0

        for i in range(len(self.remaining)):
            # if i == (len(self.remaining) - 1):
            #     print(self.remaining[idx] + "]")
            # else:
            #     print("Remaining: [" + self.remaining[idx] + ", "),
            if self.remaining[idx].arrival_time == self.current_time:
                self.waiting_queue.append(self.remaining[idx])
                self.remaining.pop(idx)
            else:
                idx = idx + 1

    # calculate turnaround time for finished process
    def is_finished(self):
        if self.running_process:
            if self.running_process.remaining_time == 0:
                # calculate turnaround time
                self.running_process.calculate_turnaround()
                # append finished process to finished processes list
                self.finished.append(self.running_process)
                # release current process for new waiting process to run
                self.running_process = None

    def scheduling(self):
        while self.running_process or self.waiting_queue or self.remaining:
            self.start()

    def turnaround_times(self):
        self.scheduling()
        times = []

        if self.finished:
            sorted_list = sorted(self.finished, key=lambda p: p.pid)
        for process in sorted_list:
            times.append(process.turnaround_time)
        return times

    def start(self):
        pass


class FIFO(Scheduler):
    def start(self):
        # check if finished/sorted
        self.is_finished()
        self.is_sorted()

        # if process is in waiting queue & no current running process..
        if self.waiting_queue and not self.running_process:
            self.running_process = self.waiting_queue.pop(0)
        self.is_started()


class SJF(Scheduler):
    def start(self):
        self.is_finished()
        self.is_sorted()

        if self.waiting_queue and not self.running_process:
            self.waiting_queue = sorted(self.waiting_queue, key=lambda process: process.remaining_time)
            self.running_process = self.waiting_queue.pop(0)

        self.is_started()


class SRT(Scheduler):
    def start(self):
        # check if finished/sorted
        self.is_finished()
        self.is_sorted()

        # if in waiting queue..
        if self.waiting_queue:
            self.waiting_queue = sorted(self.waiting_queue, key=lambda p: p.remaining_time)

            if self.running_process:
                if self.waiting_queue[0].remaining_time < self.running_process.remaining_time:
                    process = copy.deepcopy(self.running_process)
                    self.running_process = self.waiting_queue.pop(0)
                    self.waiting_queue.append(process)
            else:
                self.running_process = self.waiting_queue.pop(0)

        self.is_started()


class MLF(Scheduler):
    def __init__(self, process_list):
        super().__init__(process_list)
        self.running_process = None
        self.waiting_queue = []
        mlf_list = []

        for process in process_list:
            p = Process.MLF_Process(process.arrival_time, process.service_time, process.pid)
            mlf_list.append(p)
        self.remaining = mlf_list
        self.finished = []
        self.current_time = 0

    def is_max_time_allowed(self):
        if self.running_process:
            if self.running_process.timeslice == 0:
                self.running_process.level = self.running_process.level - 1
                self.running_process.timeslice = 2 * (self.running_process.top_level - self.running_process.level)
                # append to waiting queue
                self.waiting_queue.append(self.running_process)
                # release current process for new waiting process to run
                self.running_process = None

    def start(self):
        self.is_finished()
        self.is_sorted()
        self.is_max_time_allowed()
        # if self.waiting_queue and not self.running_process:
        #     self.running_process = self.waiting_queue.pop(0)
        if self.waiting_queue:
            # processes are sorted in FCFS order
            self.waiting_queue = sorted(self.waiting_queue, key=lambda p: p.pid)
            # sorted from top priority level to lower
            self.waiting_queue = sorted(self.waiting_queue, key=lambda p: p.level, reverse=True)
            # if there's current running process
            if self.running_process:
                if self.waiting_queue[0].level > self.running_process.level:
                    p = copy.deepcopy(self.running_process)
                    self.running_process = self.waiting_queue.pop(0)
                    self.waiting_queue.append(p)
            else:
                self.running_process = self.waiting_queue.pop(0)
        self.is_started()


def scheduling(process_list):
    fifo = FIFO(copy.deepcopy(process_list))
    sjf = SJF(copy.deepcopy(process_list))
    srt = SRT(copy.deepcopy(process_list))
    mlf = MLF(copy.deepcopy(process_list))

    schedule_list = [fifo.turnaround_times(), sjf.turnaround_times(), srt.turnaround_times(), mlf.turnaround_times()]
    return schedule_list


def read_file(inputs):
    process_list = []

    for i in range(len(inputs)):
        if i % 2 != 0:
            pid = i / 2
            p = Process.Process(inputs[i - 1], inputs[i], pid)
            process_list.append(p)
    return process_list


def write_file(file, total_turnaround_t):
    for turnaround_t in total_turnaround_t:
        avg_turnaround_time = sum(turnaround_t) / (len(turnaround_t))
        avg_turnaround_time = '{0:.2f}'.format(avg_turnaround_time)
        print(avg_turnaround_time, end=' ')
        file.write(avg_turnaround_time + ' ')

        for s in turnaround_t:
            print(s, end=' ')
            file.write(str(s))
            file.write(' ')
        print('')
        file.write('\n')
