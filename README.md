# intelligent-agents

A Java program for exploring swarming agent algorithms.

Scenario: there is an nxn grid containing t tasks and k agents. Tasks are randomly scattered around the grid. Agents start randomly at one of the four corners of the grid. In order for a task to be completed, it must be touched by 5 agents; however, with each touch, there is a 10% chance that it won't work. Agents have location sensors, but no other sensors; i.e., they can't "see" the tasks. Agents do, however, have communicators with a limited range.

When an agent finds a task (usually by random wandering), it "executes" the task and then transmits the location of the task to other agents. When an agent receives a broadcast, it must decide what to do with the information. Should it drop everything and go execute the task? Wait a while? Put it in a queue? There are many possibilities.
