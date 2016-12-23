def call(parallelism, testmode, inclusionsFile, exclusionsFile, results, image, prepare, run) {
  def splits = splitTests parallelism: parallelism, generateInclusions: true, testMode: testmode
  def branches = [:]
  for (int i = 0; i < splits.size(); i++) {
    def num = i
    def split = splits[num]
    branches["split${num}"] = {
      stage("Test Section #${num + 1}") {
        docker.image(image).inside {
          stage('Preparation') {
            prepare()
            writeFile file: (split.includes ? inclusionsFile : exclusionsFile), text: split.list.join("\n")
            writeFile file: (split.includes ? exclusionsFile : inclusionsFile), text: ''
          }
          stage('Main') {
            run()
          }
          stage('Reporting') {
            junit results
          }
        }
      }
    }
  }
  parallel branches
}
