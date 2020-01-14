import os


class Process:
    def __init__(self, arrival, service, processID):
        # process arrival time
        self.arrival_time = arrival
        # service time
        self.service_time = service
        # waiting time
        self.waiting_time = 0
        # turnaround time
        self.turnaround_time = 0
        # remaining time
        self.remaining_time = self.service_time
        # process id
        self.pid = processID

    def running(self):
        self.remaining_time = self.remaining_time - 1

    def waiting(self):
        self.waiting_time = self.waiting_time + 1

    def calculate_turnaround(self):
        self.turnaround_time = self.service_time + self.waiting_time


class MLF_Process(Process):
    def __init__(self, arrival, service, processID):
        # For MLF, assume n = 5, T = 1
        # top level = highest priority
        self.top_level = 5
        # Process enters at top level and moves down to lower level.
        self.level = self.top_level
        self.timeslice = 1
        # process arrival time
        self.arrival_time = arrival
        # service time
        self.service_time = service
        # waiting time
        self.waiting_time = 0
        # turnaround time
        self.turnaround_time = 0
        # remaining time
        self.remaining_time = self.service_time
        # process id
        self.pid = processID

    def running(self):
        self.timeslice = self.timeslice - 1
        self.remaining_time = self.remaining_time - 1

    def waiting(self):
        self.waiting_time = self.waiting_time + 1

    def calculate_turnaround(self):
        self.turnaround_time = self.service_time + self.waiting_time
