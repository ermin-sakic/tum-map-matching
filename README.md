Please feel free to read upon the documentation describing the work here:
https://vmi.lmt.ei.tum.de/publications/students/Sakic-Bachelorarbeit.pdf‎

In this thesis, an overview of modern map-matching algorithms, used to reconcile a positional
observation onto the road using geometric, topological, statistical and advanced techniques in real-
time, has been given. The usual methods of positioning, as well as the sources of errors associated
to these methods have been discussed and the motivation for the process of matching presented.
Furthermore, a scoring-based map-matching algorithm, called ST-Matching, which takes into
account spatial, topological and temporal information of the setting, has been implemented for
the mobile platform Android. The original workflow of the algorithm was additionally optimized
and offers a robust detection of input "spikes" and erroneous observations when confronted with
lower moving speed of the vehicle. For the use case on mobile phones, in addition to the GPS, it has
been extended to also use Wi-Fi and Cell ID signals for positioning purposes. The implementation
of this incremental algorithm has been tested and evaluated against multiple road tracks, in
a dense urban area, and has resulted in a high percentage of correct matches. Although the
algorithm matched the majority of observations correctly, in order to estimate the probability of
correct matches, a performance comparison against other up-to-date map-matching algorithms is
proposed. Furthermore, additional attention should be given to matching at intersections and an
execution of multiple real-world tests considered in order to make the algorithm fully usable in
practice.
